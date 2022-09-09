package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class HttpServer {

    private static HttpServer server;
    private final MainService service = new MainService();

    private HttpServer() {}

    public static HttpServer getServer() {
        if (server == null) {
            server = new HttpServer();
        }
        return server;
    }

    private void startServer() {
        ServerSocket serverSocket = null;

        Integer port = getPort();

        try {
            serverSocket = new ServerSocket(port);
            Socket clientSocket = null;
            while (Boolean.TRUE) {
                try {
                    System.out.println("Listo para recibir ...");
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    System.err.println("Accept failed.");
                    System.exit(1);
                }

                OutputStream outputStream = clientSocket.getOutputStream();

                PrintWriter out = new PrintWriter(outputStream, true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
                String inputLine, outputLine;
                ArrayList<String> request = new ArrayList<>();

                boolean firstLine = true;

                String path = "";
                String contentType = "";

                while ((inputLine = in.readLine()) != null) {
                    if (firstLine) {
                        path = inputLine.split(" ")[1];
                        firstLine = false;
                    }

                    request.add(inputLine);
                    if (!in.ready()) {
                        break;
                    }
                }

                System.out.println(path);

                String resp = "";
                URI uri = new URI(path);
                int code = 200;

                if (path.equals("/stocks")) {
                    contentType = "text/html";
                    resp = getFile("index.html");
                }
                else if (path.startsWith("/search") && uri.getQuery() != null) {
                    contentType = "application/json";
                    System.out.println("PARAMS" + uri.getQuery());
                    resp = service.getRequest(uri.getQuery().substring(5));
                }
                else {
                    contentType = "text/html";
                    resp = getFile("redirect.html");
                    code = 404;
                }


                outputLine = getHeader(code, contentType)
                            + resp
                            + inputLine;
                out.println(outputLine);

                out.close();
                in.close();
                clientSocket.close();
            }
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static int getPort() {
        int port;
        if (System.getenv("PORT") != null) {
            port = Integer.parseInt(System.getenv("PORT"));
        } else {
            port = 35000
            ;
        }

        return port;
    }

    public static String getFile(String route) throws IOException {
        Path file = FileSystems.getDefault().getPath("src/main/resources/" + route);
        Charset charset = Charset.forName("US-ASCII");
        String web = new String();
        BufferedReader reader = Files.newBufferedReader(file, charset);
        String line = null;
        while ((line = reader.readLine()) != null) {
            web += line + "\n";
        }
        return web;
    }

    public static String getHeader(int code, String type) {
        return "HTTP/1.1 " + code + " OK \r\n"
                + "Content-Type: " + type +"\r\n"
                + "\r\n";
    }

    public static void main(String[] args) {
        HttpServer.getServer().startServer();
    }
}

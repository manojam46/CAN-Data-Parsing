package assignment;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;


public class HttpServerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String file = "Hello World!";

        exchange.sendResponseHeaders(200, file.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(file.getBytes());
        os.close();
    }
}

package assignment;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.Paths;

// Embeded HTML server 
public class HttpServerHandler implements HttpHandler {

    // Handels the request fro the route "/"
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String websitePath = Paths.get(System.getProperty("user.dir"), "/website").toString(); // PWD + /website

        // Accessing the index.html file
        File indexHTML = new File(websitePath, "index.html");

        // Reading the context of the file
        BufferedReader reader = new BufferedReader(new FileReader(indexHTML));
        StringBuilder htmlFileBldr = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            htmlFileBldr.append(line).append("\n");
        }

        reader.close();

        String htmlFile = htmlFileBldr.toString();

        // Send the read file as response to user with 200 status
        exchange.sendResponseHeaders(200, htmlFile.getBytes().length);

        // Opening a stream to send data as packets
        OutputStream os = exchange.getResponseBody();
        os.write(htmlFile.getBytes());
        os.close();
    }
}

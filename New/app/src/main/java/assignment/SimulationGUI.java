package assignment;

import com.sun.net.httpserver.*;
import java.net.*;
import java.util.*;


public class SimulationGUI {
    private static int WEB_SERVER_PORT = -1;
    private static int SOCKET_PORT = 8557;
    private static SocketHandler SOCKET_SERVER;

    // Starts up the HTTP server
    public static void startHttpServer(){
        try {
            // Passing 0 will open up any open port
            HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);

            // On call for "/" will execute HttpServerHandler
            server.createContext("/", new HttpServerHandler());
            server.setExecutor(null); // creates a default executor
            server.start();

            System.out.println("Website running on: http://127.0.0.1:" + server.getAddress().getPort() + "/");
            WEB_SERVER_PORT = server.getAddress().getPort();
        } catch (Exception e) {
            System.err.println("HTTP Server Failed To Start!!");
        }
    }

    // Starts up the websocket for broadcasting
    public static void startSocket(){
        try {
            // Checkes and assigns new port based out of Web server port
            if(WEB_SERVER_PORT != -1){
                SOCKET_PORT = ++WEB_SERVER_PORT;
            }

            SOCKET_SERVER = new SocketHandler(new InetSocketAddress(SOCKET_PORT));
            SOCKET_SERVER.start();
            System.out.println("Please enter the following port number inside the website when prompted: " + SOCKET_SERVER.getPort() );
        } catch (Exception e) {
            SOCKET_PORT = -1;
            System.err.println("Communication Channel Failed To Start!!");
        }
    }

    // Passes data on to websocket to broadcast data to the connected clients
    public static void broadCastData(List<String> data){
        SOCKET_SERVER.broadCastData(data);
    }
}

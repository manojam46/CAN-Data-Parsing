package assignment;

import com.sun.net.httpserver.*;


public class SimulationGUI {
    public void startHttpServer(){
        try {
            HttpServer server = HttpServer.create(new java.net.InetSocketAddress(0), 0);
            server.createContext("/", new HttpServerHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("Website running on: http://127.0.0.1:" + server.getAddress().getPort() + "/");
        } catch (Exception e) {
            System.err.println("HTTP Server Failed To Start!!");
        }
    }

    public void startSocket(){
        try {
            
        } catch (Exception e) {
            System.err.println("Communication Channel Failed To Start!!");
        }
    }
}

package assignment;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.*;

// Handels all the socket processes
public class SocketHandler extends WebSocketServer {
    // Stores all the connectes users
    private static LinkedList<WebSocket> CONNECTIONS;

    public SocketHandler(InetSocketAddress address) {
        super(address);
        CONNECTIONS = new LinkedList<WebSocket>();
    }

    // Called when new connection is made
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // System.out.println("New connection: " + conn.getRemoteSocketAddress());
        CONNECTIONS.add(conn);
    }

    // Called when connection is closed
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    // Called when new message is sent
    @Override
    public void onMessage(WebSocket conn, String message) {
        // System.out.println("Received message: " + message + " from " + conn.getRemoteSocketAddress());

        // for(WebSocket ws: CONNECTIONS){
        //     if(ws.getRemoteSocketAddress() == conn.getRemoteSocketAddress()){
        //         continue;
        //     }

        //     ws.send(message);
        // }

        if(message.equals("START_SIM")){
            CANSimulation.startSimulation();
        }
    }

    // Called when an error occures in a connection 
    @Override
    public void onError(WebSocket conn, Exception ex) {
        // System.err.println("Error on connection: " + ex);
    }

    // Called when the socket server is started up
    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully");
    }

    // Broadcasts data to all connect client by parsing Array data to string
    public void broadCastData(List<String> data){
        String tosend = "";

        for(String itr: data){
            tosend += itr + "~";
        }

        // System.out.println(tosend);

        broadcast(tosend);
    }
}

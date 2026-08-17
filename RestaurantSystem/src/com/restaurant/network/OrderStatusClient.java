//bonus part

package com.restaurant.network;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class OrderStatusClient {

    //address of the server
    private final String host;

    //the port number used to connect to the server
    private final int port;

    //handle any message received from the server
    private final Consumer<String> onMessage;

    //shows the connection between the client and the server
    private Socket socket;

    //send messages to the server
    private PrintWriter out;

    //connection status holder
    private boolean connected = false;

    public OrderStatusClient(String host, int port, Consumer<String> onMessage) {
        this.host = host;
        this.port = port;
        this.onMessage = onMessage;
    }

    public void connect() throws IOException {

        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        connected = true;


        new Thread(() -> {

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))) {

                String line;

                //don't stop reading messages
                while ((line = in.readLine()) != null) {
                    onMessage.accept(line);
                }

            } catch (IOException e) {
                if (connected) {
                    onMessage.accept("[Disconnected from server]");
                }
            }

        }, "OrderStatusListener").start();
    }

    public void sendUpdate(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void disconnect() {
        connected = false;

        try {


            if (socket != null) {
                socket.close();
            }

        } catch (IOException ignored) {

        }
    }
}
//bonus part

package com.restaurant.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class OrderStatusServer {

    //port used by the server
    private static final int PORT = 5001;

    //used to accept client connections
    private ServerSocket serverSocket;

    //stores all connected clients
    private final List<PrintWriter> clients = new ArrayList<>();

    //connection status holder
    private boolean running = false;

    public void start() {
        running = true;

        new Thread(() -> {

            try {
                serverSocket = new ServerSocket(PORT);

                System.out.println(
                        "[Server] Order Status Server started on port " + PORT
                );

                while (running) {

                    Socket clientSocket = serverSocket.accept();

                    System.out.println(
                            "[Server] Client connected: "
                                    + clientSocket.getInetAddress()
                    );

                    //handle the connected client
                    handleClient(clientSocket);
                }

            } catch (IOException e) {

                //show the error if the server is still running
                if (running) {
                    System.out.println(
                            "[Server] Error: " + e.getMessage()
                    );
                }
            }

        }, "OrderStatusServer").start();
    }

    private void handleClient(Socket socket) {

        //create a separate thread for each client
        new Thread(() -> {

            try {

                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true
                );

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                //add the client to the connected clients list
                synchronized (clients) {
                    clients.add(out);
                }

                out.println("[Server] Connected to Order Status Feed.");

                String line;

                while ((line = in.readLine()) != null) {

                    //send the message to all connected clients
                    broadcast(line);
                }

                synchronized (clients) {
                    clients.remove(out);
                }

            } catch (IOException e) {

                System.out.println("[Server] Client disconnected.");

            } finally {

                try {
                    socket.close();
                } catch (IOException ignored) {

                }
            }

        }, "ClientHandler-" + socket.getPort()).start();
    }

    public void broadcast(String message) {

        synchronized (clients) {
            for (PrintWriter pw : clients) {
                pw.println(message);
            }
        }
    }

    public void stop() {
        running = false;

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException ignored) {

        }
    }

    public static void main(String[] args) {
        new OrderStatusServer().start();
    }
}
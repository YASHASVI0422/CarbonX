package com.carbonx.network;

import java.io.*;
import java.net.*;
import java.util.*;

public class AlertServer implements Runnable {

    private static final int PORT = 5000;

    // Once true — never reset, even on bind error
    private static volatile boolean serverStarted = false;

    private static final List<PrintWriter> clients =
            Collections.synchronizedList(new ArrayList<>());

    private volatile boolean running = true;

    @Override
    public void run() {
        // Already started in this JVM session — skip completely
        if (serverStarted) {
            System.out.println("[AlertServer] Already running — skipping.");
            return;
        }
        serverStarted = true;
        System.out.println("[AlertServer] Starting on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(1000);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[AlertServer] Client connected: "
                            + clientSocket.getInetAddress());
                    Thread t = new Thread(new ClientHandler(clientSocket));
                    t.setDaemon(true);
                    t.start();
                } catch (SocketTimeoutException e) {
                    // loop — check running flag
                }
            }
        } catch (BindException e) {
            // Port taken by previous run — that server handles alerts, fine
            System.out.println("[AlertServer] Port " + PORT
                    + " already in use — existing server will handle alerts.");
        } catch (IOException e) {
            System.err.println("[AlertServer] Error: " + e.getMessage());
        }

        System.out.println("[AlertServer] Thread exiting.");
    }

    public static void broadcastAlert(String message) {
        System.out.println("[AlertServer] Broadcasting: " + message);
        synchronized (clients) {
            for (PrintWriter writer : clients) {
                writer.println(message);
                writer.flush();
            }
        }
    }

    public static void sendCarbonAlert(String userName, double emission) {
        String message;
        if      (emission >= 60) message = "CRITICAL ALERT for " + userName
                + ": Emission = " + emission + " kg CO2! Immediate action required!";
        else if (emission >= 30) message = "HIGH ALERT for " + userName
                + ": Emission = " + emission + " kg CO2! Please reduce travel and electricity.";
        else if (emission >= 10) message = "NOTICE for " + userName
                + ": Emission = " + emission + " kg CO2. You are doing okay. Keep improving!";
        else                     message = "GREAT JOB " + userName
                + "! Emission = " + emission + " kg CO2. Excellent eco score today!";
        broadcastAlert(message);
    }

    public void stop() { running = false; }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        ClientHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                clients.add(out);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    broadcastAlert("[Echo] " + line);
                }
            } catch (IOException e) {
                System.out.println("[AlertServer] Client disconnected.");
            }
        }
    }
}
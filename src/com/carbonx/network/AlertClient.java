package com.carbonx.network;

import java.io.*;
import java.net.*;

public class AlertClient implements Runnable {

    private static final String HOST = "localhost";
    private static final int    PORT = 5000;

    private Socket           socket;
    private PrintWriter      out;
    private AlertListener    listener;
    private volatile boolean running = true;

    public AlertClient(AlertListener listener) {
        this.listener = listener;
    }

    // ── Allows DashboardView to update the listener when screen changes
    public void setListener(AlertListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(HOST, PORT);
            out    = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("[AlertClient] Connected to AlertServer.");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String message;
            while (running && (message = in.readLine()) != null) {
                final String msg = message;
                System.out.println("[AlertClient] Received: " + msg);
                if (listener != null) {
                    javafx.application.Platform.runLater(() ->
                            listener.onAlertReceived(msg));
                }
            }
        } catch (ConnectException e) {
            System.out.println("[AlertClient] Server not running. Alerts disabled.");
        } catch (IOException e) {
            if (running) {
                System.err.println("[AlertClient] Error: " + e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        if (out != null) out.println(message);
    }

    public void stop() {
        running = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("[AlertClient] Close error: " + e.getMessage());
        }
    }
}
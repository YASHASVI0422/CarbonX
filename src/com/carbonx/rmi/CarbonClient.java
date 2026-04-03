package com.carbonx.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CarbonClient extends UnicastRemoteObject implements CallbackRemote {

    private static final long serialVersionUID = 3L;

    private CarbonRemote    server;
    private CallbackHandler handler;

    public interface CallbackHandler {
        void onNotification(String message);
    }

    public CarbonClient() throws RemoteException {
        super();
    }

    public CarbonClient(CallbackHandler handler) throws RemoteException {
        super();
        this.handler = handler;
    }

    // ── Allows DashboardView to update the handler when screen changes
    public void setHandler(CallbackHandler handler) {
        this.handler = handler;
    }

    @Override
    public void receiveNotification(String message) throws RemoteException {
        System.out.println("[CarbonClient] Callback received: " + message);
        if (handler != null) {
            try {
                javafx.application.Platform.runLater(
                        () -> handler.onNotification(message));
            } catch (IllegalStateException e) {
                handler.onNotification(message);
            }
        }
    }

    public boolean connect() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (CarbonRemote) registry.lookup("CarbonService");
            server.registerCallback(this);
            System.out.println("[CarbonClient] Connected to CarbonServer via RMI.");
            System.out.println("[CarbonClient] Ping: " + server.ping());
            return true;
        } catch (Exception e) {
            System.out.println("[CarbonClient] RMI server not available: " + e.getMessage());
            return false;
        }
    }

    public double calculateAndSave(int userId, double travel,
                                   double electricity, String food) {
        if (server == null) return -1;
        try {
            return server.calculateAndSave(userId, travel, electricity, food);
        } catch (RemoteException e) {
            System.err.println("[CarbonClient] RMI call failed: " + e.getMessage());
            return -1;
        }
    }

    public double getTotalEmission(int userId) {
        if (server == null) return 0;
        try {
            return server.getTotalEmission(userId);
        } catch (RemoteException e) {
            System.err.println("[CarbonClient] RMI call failed: " + e.getMessage());
            return 0;
        }
    }

    public void disconnect() {
        try {
            if (server != null) {
                UnicastRemoteObject.unexportObject(this, true);
                System.out.println("[CarbonClient] Disconnected from RMI server.");
            }
        } catch (Exception e) {
            System.err.println("[CarbonClient] Disconnect error: " + e.getMessage());
        }
    }
}
package com.carbonx.rmi;

import com.carbonx.service.CarbonService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarbonServer extends UnicastRemoteObject implements CarbonRemote {

    private static final long serialVersionUID = 1L;

    private final CarbonService carbonService = new CarbonService();

    private final List<CallbackRemote> callbacks =
            Collections.synchronizedList(new ArrayList<>());

    protected CarbonServer() throws RemoteException {
        super();
    }

    @Override
    public double calculateAndSave(int userId, double travel,
                                   double electricity, String food)
            throws RemoteException {

        System.out.println("[CarbonServer] Received request — userId=" + userId
                + ", travel=" + travel
                + ", electricity=" + electricity
                + ", food=" + food);

        double emission = carbonService.submitEntry(userId, travel, electricity, food);
        notifyAllClients(buildCallbackMessage(emission));
        return emission;
    }

    @Override
    public double getTotalEmission(int userId) throws RemoteException {
        double total = carbonService.getTotalEmission(userId);
        System.out.println("[CarbonServer] Total emission for userId="
                + userId + " is " + total + " kg CO2");
        return total;
    }

    @Override
    public void registerCallback(CallbackRemote callback) throws RemoteException {
        callbacks.add(callback);
        System.out.println("[CarbonServer] Client callback registered. "
                + "Total clients: " + callbacks.size());
    }

    @Override
    public String ping() throws RemoteException {
        return "CarbonServer is alive!";
    }

    private String buildCallbackMessage(double emission) {
        if      (emission >= 60) return "CRITICAL: Emission = " + emission + " kg CO2! Immediate action required.";
        else if (emission >= 30) return "HIGH: Emission = " + emission + " kg CO2. Reduce travel and electricity.";
        else if (emission >= 10) return "MODERATE: Emission = " + emission + " kg CO2. Keep improving!";
        else                     return "EXCELLENT: Emission = " + emission + " kg CO2. Great eco score!";
    }

    private void notifyAllClients(String message) {
        List<CallbackRemote> deadClients = new ArrayList<>();
        synchronized (callbacks) {
            for (CallbackRemote cb : callbacks) {
                try {
                    cb.receiveNotification(message);
                } catch (RemoteException e) {
                    System.out.println("[CarbonServer] Client disconnected — removing.");
                    deadClients.add(cb);
                }
            }
        }
        callbacks.removeAll(deadClients);
    }

    // ── FIXED: handles port 1099 already in use on screen navigation
    public static void startServer() {
        try {
            CarbonServer server = new CarbonServer();

            Registry registry;
            try {
                // First run — create fresh registry
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("[CarbonServer] RMI registry created on port 1099.");
            } catch (RemoteException e) {
                // Registry already exists (e.g. logout → login) — reuse it
                registry = LocateRegistry.getRegistry(1099);
                System.out.println("[CarbonServer] Reusing existing RMI registry on port 1099.");
            }

            registry.rebind("CarbonService", server);
            System.out.println("[CarbonServer] RMI Server started on port 1099.");
            System.out.println("[CarbonServer] Bound as 'CarbonService' in registry.");

        } catch (RemoteException e) {
            System.err.println("[CarbonServer] Failed to start: " + e.getMessage());
        }
    }
}
package com.carbonx.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Remote Interface.
 * Declares methods the client can call on the remote server.
 * Every method must throw RemoteException — RMI requirement.
 */
public interface CarbonRemote extends Remote {

    /**
     * Calculate emission remotely and save to DB.
     * Returns the total emission value.
     */
    double calculateAndSave(int userId, double travel,
                            double electricity, String food)
            throws RemoteException;

    /**
     * Fetch total emission for a user from the server.
     */
    double getTotalEmission(int userId) throws RemoteException;

    /**
     * Register a client callback so server can push notifications.
     */
    void registerCallback(CallbackRemote callback) throws RemoteException;

    /**
     * Server ping — used to verify connection is alive.
     */
    String ping() throws RemoteException;
}
package com.carbonx.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Callback Interface.
 * Implemented by the CLIENT so the SERVER can call back to it.
 * This is the key difference from Phase 3 sockets — server invokes client methods.
 */
public interface CallbackRemote extends Remote {

    /**
     * Server calls this method on the client to push a notification.
     */
    void receiveNotification(String message) throws RemoteException;
}
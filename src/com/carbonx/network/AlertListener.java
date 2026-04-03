package com.carbonx.network;

/**
 * Callback interface for receiving socket alerts.
 * Implemented by UI components that want to display alerts.
 */
public interface AlertListener {
    void onAlertReceived(String message);
}
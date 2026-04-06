package com.carbonx.ui;

import com.carbonx.model.User;
import com.carbonx.rmi.CarbonServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("CarbonX – Smart Carbon Footprint Intelligence System");
        primaryStage.setWidth(900);
        primaryStage.setHeight(650);
        primaryStage.setResizable(false);

        // Start RMI Server in background
        Thread rmiThread = new Thread(CarbonServer::startServer);
        rmiThread.setDaemon(true);
        rmiThread.start();

        showLogin();
        primaryStage.show();
    }

    public static void showLogin() {
        LoginView view = new LoginView();
        Scene scene = new Scene(view.getView(), 900, 650);
        scene.setFill(Color.web("#0d1117"));
        primaryStage.setScene(scene);
    }

    public static void showDashboard(User user) {
        DashboardView view = new DashboardView(user);
        Scene scene = new Scene(view.getView(), 900, 650);
        primaryStage.setScene(scene);
    }

    public static void showLeaderboard(User user) {
        LeaderboardView view = new LeaderboardView(user);
        Scene scene = new Scene(view.getView(), 900, 650);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

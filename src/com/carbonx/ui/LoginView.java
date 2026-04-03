package com.carbonx.ui;

import com.carbonx.model.User;
import com.carbonx.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.util.Optional;

public class LoginView {

    private final UserService userService = new UserService();
    private boolean isLoginMode = true;

    // Extra fields for register
    private TextField nameField = new TextField();
    private TextField cityField = new TextField();

    public VBox getView() {

        // ── Root
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #0d1117;");

        // ── Logo / Title
        Text logo = new Text("CarbonX");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        logo.setFill(Color.web("#39d353"));

        Text subtitle = new Text("Smart Carbon Footprint Intelligence System");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setFill(Color.web("#8b949e"));

        Text sdg = new Text("SDG 13 – Climate Action");
        sdg.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        sdg.setFill(Color.web("#f0883e"));

        // ── Card
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(30));
        card.setMaxWidth(400);
        card.setStyle("-fx-background-color: #161b22;" +
                      "-fx-border-color: #30363d;" +
                      "-fx-border-radius: 10;" +
                      "-fx-background-radius: 10;");

        // ── Mode label
        Label modeLabel = new Label("Login to your account");
        modeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        modeLabel.setTextFill(Color.web("#c9d1d9"));

        // ── Fields
        TextField emailField    = createField("Email address");
        PasswordField passField = createPassField("Password");

        nameField = createField("Full name");
        cityField = createField("City");
        nameField.setVisible(false); nameField.setManaged(false);
        cityField.setVisible(false); cityField.setManaged(false);

        // ── Message label (shows errors/success)
        Label msgLabel = new Label("");
        msgLabel.setFont(Font.font("Arial", 13));
        msgLabel.setTextFill(Color.web("#f85149"));
        msgLabel.setWrapText(true);

        // ── Action button
        Button actionBtn = new Button("Login");
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        actionBtn.setStyle("-fx-background-color: #238636;" +
                           "-fx-text-fill: white;" +
                           "-fx-font-size: 14px;" +
                           "-fx-padding: 10;" +
                           "-fx-background-radius: 6;" +
                           "-fx-cursor: hand;");

        // ── Toggle link
        Hyperlink toggleLink = new Hyperlink("Don't have an account? Register");
        toggleLink.setTextFill(Color.web("#58a6ff"));
        toggleLink.setStyle("-fx-border-color: transparent;");

        // ── Toggle between login and register
        toggleLink.setOnAction(e -> {
            isLoginMode = !isLoginMode;
            if (isLoginMode) {
                modeLabel.setText("Login to your account");
                actionBtn.setText("Login");
                toggleLink.setText("Don't have an account? Register");
                nameField.setVisible(false); nameField.setManaged(false);
                cityField.setVisible(false); cityField.setManaged(false);
            } else {
                modeLabel.setText("Create an account");
                actionBtn.setText("Register");
                toggleLink.setText("Already have an account? Login");
                nameField.setVisible(true); nameField.setManaged(true);
                cityField.setVisible(true); cityField.setManaged(true);
            }
            msgLabel.setText("");
        });

        // ── Action button logic
        actionBtn.setOnAction(e -> {
            String email    = emailField.getText().trim();
            String password = passField.getText().trim();

            if (isLoginMode) {
                // LOGIN
                Optional<User> result = userService.login(email, password);
                if (result.isPresent()) {
                    msgLabel.setTextFill(Color.web("#39d353"));
                    msgLabel.setText("Login successful! Loading dashboard...");
                    MainApp.showDashboard(result.get());
                } else {
                    msgLabel.setTextFill(Color.web("#f85149"));
                    msgLabel.setText("Invalid email or password. Try again.");
                }
            } else {
                // REGISTER
                String name = nameField.getText().trim();
                String city = cityField.getText().trim();
                boolean success = userService.register(name, email, password, city);
                if (success) {
                    msgLabel.setTextFill(Color.web("#39d353"));
                    msgLabel.setText("Registered successfully! Please login.");
                    isLoginMode = true;
                    modeLabel.setText("Login to your account");
                    actionBtn.setText("Login");
                    toggleLink.setText("Don't have an account? Register");
                    nameField.setVisible(false); nameField.setManaged(false);
                    cityField.setVisible(false); cityField.setManaged(false);
                } else {
                    msgLabel.setTextFill(Color.web("#f85149"));
                    msgLabel.setText("Registration failed. Email may already exist.");
                }
            }
        });

        card.getChildren().addAll(
                modeLabel, nameField, cityField,
                emailField, passField,
                msgLabel, actionBtn, toggleLink
        );

        root.getChildren().addAll(logo, subtitle, sdg, card);
        return root;
    }

    private TextField createField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #0d1117;" +
                    "-fx-text-fill: #c9d1d9;" +
                    "-fx-border-color: #30363d;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 8;" +
                    "-fx-font-size: 13px;");
        return tf;
    }

    private PasswordField createPassField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle("-fx-background-color: #0d1117;" +
                    "-fx-text-fill: #c9d1d9;" +
                    "-fx-border-color: #30363d;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 8;" +
                    "-fx-font-size: 13px;");
        return pf;
    }
}
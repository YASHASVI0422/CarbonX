package com.carbonx.ui;

import com.carbonx.model.CarbonData;
import com.carbonx.model.User;
import com.carbonx.network.AlertClient;
import com.carbonx.network.AlertServer;
import com.carbonx.rmi.CarbonClient;
import com.carbonx.service.CarbonService;
import com.carbonx.service.RecommendationEngine;
import com.carbonx.util.CarbonCalculator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.util.List;
import java.util.Map;

public class DashboardView {

    private final User                 user;
    private final CarbonService        carbonService        = new CarbonService();
    private final RecommendationEngine recommendationEngine = new RecommendationEngine();

    private Label alertLabel;
    private VBox  recPanel;

    // ── Static — shared across all DashboardView instances ──────────────
    private static AlertClient  sharedAlertClient = null;
    private static CarbonClient sharedRmiClient   = null;
    private static boolean      networkStarted    = false;
    // ─────────────────────────────────────────────────────────────────────

    public DashboardView(User user) {
        this.user = user;
    }

    // Called only once per app session
    private void initNetworkOnce() {
        if (networkStarted) return;
        networkStarted = true;

        // Start AlertServer
        Thread st = new Thread(new AlertServer());
        st.setDaemon(true);
        st.start();

        // Start AlertClient
        sharedAlertClient = new AlertClient(message -> {
            if (alertLabel != null) {
                alertLabel.setText("ALERT: " + message);
                alertLabel.setVisible(true);
            }
        });
        Thread ct = new Thread(sharedAlertClient);
        ct.setDaemon(true);
        ct.start();

        // Start RMI Client
        try {
            sharedRmiClient = new CarbonClient(message -> {
                if (alertLabel != null)
                    alertLabel.setText("RMI: " + message);
            });
            Thread rt = new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                sharedRmiClient.connect();
            });
            rt.setDaemon(true);
            rt.start();
        } catch (Exception e) {
            System.out.println("[Dashboard] RMI client init failed: " + e.getMessage());
        }
    }

    public BorderPane getView() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0d1117;");

        // ── Alert banner — must be created BEFORE initNetworkOnce()
        alertLabel = new Label("");
        alertLabel.setMaxWidth(Double.MAX_VALUE);
        alertLabel.setPadding(new Insets(10, 20, 10, 20));
        alertLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        alertLabel.setTextFill(Color.web("#0d1117"));
        alertLabel.setStyle("-fx-background-color: #f0883e;");
        alertLabel.setVisible(false);
        alertLabel.setWrapText(true);

        // Now safe to init network (alertLabel is ready)
        initNetworkOnce();

        // Update shared client listeners to point to THIS screen's alertLabel
        if (sharedAlertClient != null) {
            sharedAlertClient.setListener(message -> {
                alertLabel.setText("ALERT: " + message);
                alertLabel.setVisible(true);
            });
        }
        if (sharedRmiClient != null) {
            sharedRmiClient.setHandler(message ->
                    alertLabel.setText("RMI: " + message));
        }

        // ── Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #161b22;" +
                        "-fx-border-color: #30363d;" +
                        "-fx-border-width: 0 0 1 0;");

        Text appName = new Text("CarbonX");
        appName.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        appName.setFill(Color.web("#39d353"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text userInfo = new Text("Welcome, " + user.getName() + "  |  " + user.getCity());
        userInfo.setFont(Font.font("Arial", 13));
        userInfo.setFill(Color.web("#8b949e"));

        Button leaderboardBtn = new Button("Leaderboard");
        leaderboardBtn.setStyle("-fx-background-color: #1f6feb;" +
                                "-fx-text-fill: white;" +
                                "-fx-border-radius: 6;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 6 12;");
        leaderboardBtn.setOnAction(e -> MainApp.showLeaderboard(user));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #21262d;" +
                           "-fx-text-fill: #c9d1d9;" +
                           "-fx-border-color: #30363d;" +
                           "-fx-border-radius: 6;" +
                           "-fx-background-radius: 6;" +
                           "-fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            // Stop clients on logout — reset static state for next login
            if (sharedAlertClient != null) { sharedAlertClient.stop(); sharedAlertClient = null; }
            if (sharedRmiClient   != null) { sharedRmiClient.disconnect(); sharedRmiClient = null; }
            networkStarted = false;
            MainApp.showLogin();
        });

        topBar.getChildren().addAll(appName, spacer, userInfo,
                new Label("   "), leaderboardBtn,
                new Label("  "), logoutBtn);
        root.setTop(topBar);

        // ── Center
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");
        scroll.setFitToWidth(true);

        VBox center = new VBox(20);
        center.setPadding(new Insets(24));

        double total    = carbonService.getTotalEmission(user.getId());
        double ecoScore = CarbonCalculator.calculateEcoScore(total);
        List<CarbonData> history = carbonService.getHistory(user.getId());

        HBox statsRow = buildStatsRow(total, ecoScore, history.size());
        BarChart<String, Number> barChart = buildBarChart();

        recPanel = buildRecommendationsPanel();

        VBox calcSection = new CalculatorView(
                user, carbonService, barChart,
                sharedAlertClient, user.getName(),
                this::refreshRecommendations
        ).getSection();

        center.getChildren().addAll(alertLabel, statsRow, calcSection, recPanel, barChart);
        scroll.setContent(center);
        root.setCenter(scroll);

        return root;
    }

    private VBox buildRecommendationsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #161b22;" +
                       "-fx-border-color: #30363d;" +
                       "-fx-border-radius: 10;" +
                       "-fx-background-radius: 10;");
        Label heading = new Label("Personalised Recommendations");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        heading.setTextFill(Color.web("#c9d1d9"));
        panel.getChildren().add(heading);
        for (String rec : recommendationEngine.getRecommendations(user.getId())) {
            Label lbl = new Label("  •  " + rec);
            lbl.setFont(Font.font("Arial", 13));
            lbl.setTextFill(Color.web("#8b949e"));
            lbl.setWrapText(true);
            panel.getChildren().add(lbl);
        }
        return panel;
    }

    private void refreshRecommendations() {
        recPanel.getChildren().clear();
        Label heading = new Label("Personalised Recommendations");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        heading.setTextFill(Color.web("#c9d1d9"));
        recPanel.getChildren().add(heading);
        for (String rec : recommendationEngine.getRecommendations(user.getId())) {
            Label lbl = new Label("  •  " + rec);
            lbl.setFont(Font.font("Arial", 13));
            lbl.setTextFill(Color.web("#8b949e"));
            lbl.setWrapText(true);
            recPanel.getChildren().add(lbl);
        }
    }

    private HBox buildStatsRow(double total, double ecoScore, int entries) {
        HBox row = new HBox(16);
        VBox c1 = statCard("Total CO2 Emitted", total    + " kg",    "#f85149");
        VBox c2 = statCard("Eco Score",          ecoScore + " / 100","#39d353");
        VBox c3 = statCard("Total Entries",      entries  + " days",  "#58a6ff");
        VBox c4 = statCard("SDG Goal",           "Climate Action",    "#f0883e");
        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);
        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private VBox statCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #161b22;" +
                      "-fx-border-color: #30363d;" +
                      "-fx-border-radius: 8;" +
                      "-fx-background-radius: 8;");
        Label t = new Label(title);
        t.setFont(Font.font("Arial", 12));
        t.setTextFill(Color.web("#8b949e"));
        Label v = new Label(value);
        v.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        v.setTextFill(Color.web(color));
        card.getChildren().addAll(t, v);
        return card;
    }

    private BarChart<String, Number> buildBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("CO2 (kg)");
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Daily Carbon Emissions");
        chart.setStyle("-fx-background-color: #161b22;");
        chart.setLegendVisible(false);
        refreshChart(chart);
        return chart;
    }

    public void refreshChart(BarChart<String, Number> chart) {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Double> daily = carbonService.getDailyEmissions(user.getId());
        daily.forEach((date, value) ->
                series.getData().add(new XYChart.Data<>(date, value)));
        chart.getData().add(series);
    }
}
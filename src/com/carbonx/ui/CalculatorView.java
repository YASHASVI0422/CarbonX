package com.carbonx.ui;

import com.carbonx.model.User;
import com.carbonx.network.AlertClient;
import com.carbonx.network.AlertServer;
import com.carbonx.service.CarbonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.util.Map;

public class CalculatorView {

    private final User          user;
    private final CarbonService carbonService;
    private final BarChart<String, Number> chart;
    private final AlertClient   alertClient;
    private final String        userName;
    private final Runnable      onNewEntry;   // called after each save

    public CalculatorView(User user, CarbonService carbonService,
                          BarChart<String, Number> chart,
                          AlertClient alertClient, String userName,
                          Runnable onNewEntry) {
        this.user          = user;
        this.carbonService = carbonService;
        this.chart         = chart;
        this.alertClient   = alertClient;
        this.userName      = userName;
        this.onNewEntry    = onNewEntry;
    }

    public VBox getSection() {

        VBox section = new VBox(16);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: #161b22;" +
                         "-fx-border-color: #30363d;" +
                         "-fx-border-radius: 10;" +
                         "-fx-background-radius: 10;");

        Label heading = new Label("Calculate Today's Carbon Footprint");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        heading.setTextFill(Color.web("#c9d1d9"));

        HBox inputRow = new HBox(12);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        TextField travelField      = inputField("Travel (km)");
        TextField electricityField = inputField("Electricity (units)");

        ComboBox<String> foodBox = new ComboBox<>();
        foodBox.getItems().addAll("veg", "nonveg", "vegan");
        foodBox.setValue("veg");
        foodBox.setStyle("-fx-background-color: #0d1117;" +
                         "-fx-text-fill: #c9d1d9;" +
                         "-fx-border-color: #30363d;" +
                         "-fx-border-radius: 6;");

        Button calcBtn = new Button("Calculate + Save");
        calcBtn.setStyle("-fx-background-color: #1f6feb;" +
                         "-fx-text-fill: white;" +
                         "-fx-font-size: 13px;" +
                         "-fx-padding: 8 16;" +
                         "-fx-background-radius: 6;" +
                         "-fx-cursor: hand;");

        Label travelLbl = new Label("Travel:");
        travelLbl.setTextFill(Color.web("#8b949e"));
        Label elecLbl = new Label("Electricity:");
        elecLbl.setTextFill(Color.web("#8b949e"));
        Label foodLbl = new Label("Food:");
        foodLbl.setTextFill(Color.web("#8b949e"));

        inputRow.getChildren().addAll(
                travelLbl, travelField,
                elecLbl,   electricityField,
                foodLbl,   foodBox,
                calcBtn
        );

        Label resultLabel = new Label("");
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        resultLabel.setTextFill(Color.web("#39d353"));

        Label recLabel = new Label("");
        recLabel.setFont(Font.font("Arial", 13));
        recLabel.setTextFill(Color.web("#f0883e"));
        recLabel.setWrapText(true);

        calcBtn.setOnAction(e -> {
            try {
                double travel      = Double.parseDouble(travelField.getText().trim());
                double electricity = Double.parseDouble(electricityField.getText().trim());
                String food        = foodBox.getValue();

                double emission = carbonService.submitEntry(
                        user.getId(), travel, electricity, food);

                resultLabel.setTextFill(Color.web("#39d353"));
                resultLabel.setText("Today's Emission: " + emission + " kg CO2");
                recLabel.setText(getRecommendation(emission));

                // Trigger socket alert
                AlertServer.sendCarbonAlert(userName, emission);

                // Refresh bar chart (fixed — no new DashboardView)
                refreshChart();

                // Notify dashboard to refresh recommendations panel
                if (onNewEntry != null) onNewEntry.run();

                travelField.clear();
                electricityField.clear();

            } catch (NumberFormatException ex) {
                resultLabel.setTextFill(Color.web("#f85149"));
                resultLabel.setText("Please enter valid numbers.");
            }
        });

        section.getChildren().addAll(heading, inputRow, resultLabel, recLabel);
        return section;
    }

    // Fixed chart refresh — no longer creates a new DashboardView
    private void refreshChart() {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Double> daily = carbonService.getDailyEmissions(user.getId());
        daily.forEach((date, value) ->
                series.getData().add(new XYChart.Data<>(date, value)));
        chart.getData().add(series);
    }

    private String getRecommendation(double emission) {
        if      (emission < 10) return "Great job! Your carbon footprint is very low. Keep it up!";
        else if (emission < 30) return "Tip: Consider public transport to reduce travel emissions.";
        else if (emission < 60) return "Warning: High emissions! Reduce electricity and eat vegan.";
        else                    return "Alert: Very high footprint! Switch to renewable energy sources.";
    }

    private TextField inputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(130);
        tf.setStyle("-fx-background-color: #0d1117;" +
                    "-fx-text-fill: #c9d1d9;" +
                    "-fx-border-color: #30363d;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 7;");
        return tf;
    }
}
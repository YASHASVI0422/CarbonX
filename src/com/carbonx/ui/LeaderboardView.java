package com.carbonx.ui;

import com.carbonx.model.LeaderboardEntry;
import com.carbonx.model.User;
import com.carbonx.service.LeaderboardService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.util.List;

public class LeaderboardView {

    private final User               user;
    private final LeaderboardService leaderboardService = new LeaderboardService();

    public LeaderboardView(User user) {
        this.user = user;
    }

    public BorderPane getView() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0d1117;");

        // ── Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #161b22;" +
                        "-fx-border-color: #30363d;" +
                        "-fx-border-width: 0 0 1 0;");

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #21262d;" +
                         "-fx-text-fill: #c9d1d9;" +
                         "-fx-border-color: #30363d;" +
                         "-fx-border-radius: 6;" +
                         "-fx-background-radius: 6;" +
                         "-fx-cursor: hand;");
        backBtn.setOnAction(e -> MainApp.showDashboard(user));

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Text title = new Text("Eco Leaderboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setFill(Color.web("#39d353"));

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #238636;" +
                            "-fx-text-fill: white;" +
                            "-fx-border-radius: 6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-cursor: hand;");

        topBar.getChildren().addAll(backBtn, spacer1, title, spacer2, refreshBtn);
        root.setTop(topBar);

        // ── Center
        VBox center = new VBox(16);
        center.setPadding(new Insets(24));

        // User rank card
        int userRank = leaderboardService.getUserRank(user.getId());
        center.getChildren().add(buildRankCard(userRank));

        // Badge legend
        center.getChildren().add(buildLegend());

        // Table
        TableView<LeaderboardEntry> table = buildTable();
        table.setItems(loadData());

        // Highlight logged-in user's row
        table.setRowFactory(tv -> new TableRow<LeaderboardEntry>() {
            @Override
            protected void updateItem(LeaderboardEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getUserId() == user.getId()) {
                    setStyle("-fx-background-color: #1c3a2a;");
                } else {
                    setStyle("");
                }
            }
        });

        refreshBtn.setOnAction(e -> table.setItems(loadData()));

        center.getChildren().add(table);

        ScrollPane scroll = new ScrollPane(center);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");
        root.setCenter(scroll);

        return root;
    }

    // ── Table builder
    private TableView<LeaderboardEntry> buildTable() {

        TableView<LeaderboardEntry> table = new TableView<>();
        table.setStyle("-fx-background-color: #161b22;" +
                       "-fx-border-color: #30363d;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(380);

        // Rank column with medals
        TableColumn<LeaderboardEntry, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        rankCol.setMaxWidth(80);
        rankCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) { setText(null); return; }
                if      (r == 1) setText("Gold   #1");
                else if (r == 2) setText("Silver #2");
                else if (r == 3) setText("Bronze #3");
                else             setText("#" + r);
                setStyle("-fx-text-fill: #c9d1d9; -fx-alignment: CENTER;");
            }
        });

        // Name
        TableColumn<LeaderboardEntry, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        styleTextCol(nameCol, "#c9d1d9");

        // City
        TableColumn<LeaderboardEntry, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
        styleTextCol(cityCol, "#8b949e");

        // Eco Score (colour coded)
        TableColumn<LeaderboardEntry, Double> ecoCol = new TableColumn<>("Eco Score");
        ecoCol.setCellValueFactory(new PropertyValueFactory<>("ecoScore"));
        ecoCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) { setText(null); return; }
                setText(score + " / 100");
                String c = score >= 80 ? "#39d353" :
                           score >= 60 ? "#58a6ff" :
                           score >= 40 ? "#f0883e" : "#f85149";
                setStyle("-fx-text-fill: " + c + "; -fx-font-weight: bold;");
            }
        });

        // Total emission
        TableColumn<LeaderboardEntry, Double> emCol = new TableColumn<>("Total CO2 (kg)");
        emCol.setCellValueFactory(new PropertyValueFactory<>("totalEmission"));
        styleTextCol(emCol, "#f85149");

        // Badge
        TableColumn<LeaderboardEntry, String> badgeCol = new TableColumn<>("Badge");
        badgeCol.setCellValueFactory(new PropertyValueFactory<>("badge"));
        badgeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String b, boolean empty) {
                super.updateItem(b, empty);
                if (empty || b == null) { setText(null); return; }
                setText(b);
                String c = b.equals("Eco Hero")     ? "#39d353" :
                           b.equals("Green")         ? "#58a6ff" :
                           b.equals("Average")       ? "#f0883e" : "#f85149";
                setStyle("-fx-text-fill: " + c + "; -fx-font-weight: bold;");
            }
        });

        table.getColumns().addAll(rankCol, nameCol, cityCol, ecoCol, emCol, badgeCol);
        return table;
    }

    private <T> void styleTextCol(TableColumn<LeaderboardEntry, T> col, String color) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(item.toString()); setStyle("-fx-text-fill: " + color + ";"); }
            }
        });
    }

    private ObservableList<LeaderboardEntry> loadData() {
        List<LeaderboardEntry> entries = leaderboardService.getLeaderboard();
        return FXCollections.observableArrayList(entries);
    }

    // ── User rank card
    private HBox buildRankCard(int rank) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #1c3a2a;" +
                      "-fx-border-color: #39d353;" +
                      "-fx-border-radius: 8;" +
                      "-fx-background-radius: 8;");

        Text rankTxt = new Text(rank > 0 ? "Your Rank: #" + rank : "Submit an entry to appear on the leaderboard");
        rankTxt.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        rankTxt.setFill(Color.web("#39d353"));

        Text nameTxt = new Text("  |  " + user.getName() + "  from  " + user.getCity());
        nameTxt.setFont(Font.font("Arial", 14));
        nameTxt.setFill(Color.web("#c9d1d9"));

        card.getChildren().addAll(rankTxt, nameTxt);
        return card;
    }

    // ── Badge legend
    private HBox buildLegend() {
        HBox legend = new HBox(12);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
            legendItem("Eco Hero",     "Score >= 80", "#39d353"),
            legendItem("Green",        "Score >= 60", "#58a6ff"),
            legendItem("Average",      "Score >= 40", "#f0883e"),
            legendItem("High Emitter", "Score < 40",  "#f85149")
        );
        return legend;
    }

    private HBox legendItem(String badge, String range, String color) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(6, 12, 6, 12));
        item.setStyle("-fx-background-color: #161b22;" +
                      "-fx-border-color: #30363d;" +
                      "-fx-border-radius: 6;" +
                      "-fx-background-radius: 6;");
        Label b = new Label(badge);
        b.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        b.setTextFill(Color.web(color));
        Label r = new Label(" (" + range + ")");
        r.setFont(Font.font("Arial", 11));
        r.setTextFill(Color.web("#8b949e"));
        item.getChildren().addAll(b, r);
        return item;
    }
}
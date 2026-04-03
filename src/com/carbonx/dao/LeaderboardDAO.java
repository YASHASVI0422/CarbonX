package com.carbonx.dao;

import com.carbonx.model.LeaderboardEntry;
import com.carbonx.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardDAO {

    // Save or update a user's eco score
    public boolean saveScore(int userId, double ecoScore, double totalEmission) {
        String sql = "INSERT INTO leaderboard (user_id, eco_score) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE eco_score = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, userId);
            ps.setDouble(2, ecoScore);
            ps.setDouble(3, ecoScore);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("LeaderboardDAO save error: " + e.getMessage());
            return false;
        }
    }

    // Get all users ranked by eco score (highest first)
    public List<LeaderboardEntry> getLeaderboard() {
        List<LeaderboardEntry> list = new ArrayList<>();

        String sql = "SELECT l.user_id, u.name, u.city, l.eco_score, " +
                     "COALESCE(SUM(c.total_emission), 0) AS total_emission " +
                     "FROM leaderboard l " +
                     "JOIN users u ON l.user_id = u.id " +
                     "LEFT JOIN carbon_data c ON c.user_id = l.user_id " +
                     "GROUP BY l.user_id, u.name, u.city, l.eco_score " +
                     "ORDER BY l.eco_score DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int rank = 1;
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry(
                        rs.getInt   ("user_id"),
                        rs.getString("name"),
                        rs.getString("city"),
                        rs.getDouble("eco_score"),
                        rs.getDouble("total_emission")
                );
                entry.setRank(rank++);
                list.add(entry);
            }
        } catch (SQLException e) {
            System.err.println("LeaderboardDAO fetch error: " + e.getMessage());
        }
        return list;
    }

    // Get rank of a specific user
    public int getUserRank(int userId) {
        String sql = "SELECT COUNT(*) + 1 AS rank FROM leaderboard " +
                     "WHERE eco_score > (SELECT eco_score FROM leaderboard " +
                     "WHERE user_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("rank");
        } catch (SQLException e) {
            System.err.println("LeaderboardDAO rank error: " + e.getMessage());
        }
        return -1;
    }
}
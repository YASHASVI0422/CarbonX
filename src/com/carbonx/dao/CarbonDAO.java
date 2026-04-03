package com.carbonx.dao;

import com.carbonx.model.CarbonData;
import com.carbonx.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarbonDAO {

    // Insert carbon entry into database
    public boolean save(CarbonData data) {
        String sql = "INSERT INTO carbon_data (user_id, travel, electricity, food, total_emission, date) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt   (1, data.getUserId());
            ps.setDouble(2, data.getTravel());
            ps.setDouble(3, data.getElectricity());
            ps.setString(4, data.getFood());
            ps.setDouble(5, data.getTotalEmission());
            ps.setDate  (6, Date.valueOf(data.getDate()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }

    // Get all entries for a specific user
    public List<CarbonData> getByUserId(int userId) {
        List<CarbonData> list = new ArrayList<>();
        String sql = "SELECT * FROM carbon_data WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CarbonData cd = new CarbonData();
                cd.setId           (rs.getInt   ("id"));
                cd.setUserId       (rs.getInt   ("user_id"));
                cd.setTravel       (rs.getDouble("travel"));
                cd.setElectricity  (rs.getDouble("electricity"));
                cd.setFood         (rs.getString("food"));
                cd.setTotalEmission(rs.getDouble("total_emission"));
                cd.setDate         (rs.getDate  ("date").toLocalDate());
                list.add(cd);
            }

        } catch (SQLException e) {
            System.err.println("Fetch error: " + e.getMessage());
        }

        return list;
    }
}
package com.carbonx.dao;

import com.carbonx.model.User;
import com.carbonx.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, city) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getCity());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Register error: " + e.getMessage());
            return false;
        }
    }

    public Optional<User> loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId      (rs.getInt   ("id"));
                u.setName    (rs.getString("name"));
                u.setEmail   (rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setCity    (rs.getString("city"));
                return Optional.of(u);
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Fetch all users — used by LeaderboardService stream pipeline.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement  st   = conn.createStatement();
             ResultSet  rs   = st.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User();
                u.setId      (rs.getInt   ("id"));
                u.setName    (rs.getString("name"));
                u.setEmail   (rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setCity    (rs.getString("city"));
                users.add(u);
            }
        } catch (SQLException e) {
            System.err.println("getAllUsers error: " + e.getMessage());
        }
        return users;
    }
}
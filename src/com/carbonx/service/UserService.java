package com.carbonx.service;

import com.carbonx.dao.UserDAO;
import com.carbonx.model.User;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public boolean register(String name, String email, String password, String city) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            System.out.println("All fields are required.");
            return false;
        }
        User user = new User(name, email, password, city);
        return userDAO.registerUser(user);
    }

    public Optional<User> login(String email, String password) {
        return userDAO.loginUser(email, password);
    }
}
package com.carbonx.main;

import com.carbonx.model.User;
import com.carbonx.service.CarbonService;
import com.carbonx.service.UserService;
import java.util.Optional;

public class TestDB {

    public static void main(String[] args) {

        UserService   userService   = new UserService();
        CarbonService carbonService = new CarbonService();

        // TEST 1: Register a new user
        System.out.println("=== TEST 1: Register ===");
        boolean registered = userService.register("Yashasvi", "yashasvi@test.com", "pass123", "Delhi");
        System.out.println("Registered: " + registered);

        // TEST 2: Login
        System.out.println("\n=== TEST 2: Login ===");
        Optional<User> userOpt = userService.login("yashasvi@test.com", "pass123");
        userOpt.ifPresent(u -> System.out.println("Welcome: " + u.getName() + " from " + u.getCity()));

        // TEST 3: Submit carbon entry
        System.out.println("\n=== TEST 3: Carbon Entry ===");
        if (userOpt.isPresent()) {
            int userId = userOpt.get().getId();
            double emission = carbonService.submitEntry(userId, 50.0, 30.0, "nonveg");
            System.out.println("Emission calculated: " + emission + " kg CO2");

            // TEST 4: Streams
            System.out.println("\n=== TEST 4: Streams ===");
            System.out.println("Total emission: " + carbonService.getTotalEmission(userId));
            System.out.println("Daily map:      " + carbonService.getDailyEmissions(userId));
        }

        System.out.println("\n=== ALL TESTS DONE ===");
    }
}
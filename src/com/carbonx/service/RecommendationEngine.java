package com.carbonx.service;

import com.carbonx.dao.CarbonDAO;
import com.carbonx.model.CarbonData;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule-based recommendation engine.
 * Analyzes latest entry + 7-day trend using Streams.
 */
public class RecommendationEngine {

    private final CarbonDAO carbonDAO = new CarbonDAO();

    public List<String> getRecommendations(int userId) {

        List<String> recs = new ArrayList<>();
        List<CarbonData> history = carbonDAO.getByUserId(userId);

        if (history.isEmpty()) {
            recs.add("Submit your first carbon entry to get personalised recommendations!");
            return recs;
        }

        // Latest entry (list is ordered by date DESC)
        CarbonData latest    = history.get(0);
        double     travel    = latest.getTravel();
        double     elec      = latest.getElectricity();
        String     food      = latest.getFood();
        double     emission  = latest.getTotalEmission();

        // ── Rule 1: Travel
        if (travel > 100) {
            recs.add("Your travel was " + travel + " km. Work from home or carpool — cuts travel emissions by 70%.");
        } else if (travel > 50) {
            recs.add("Switch to public transport for trips over 10 km — reduces travel emission by 45%.");
        } else if (travel > 0) {
            recs.add("Good travel habits! For trips under 5 km, cycling saves 100% of travel emissions.");
        }

        // ── Rule 2: Electricity
        if (elec > 100) {
            recs.add("Electricity usage of " + elec + " units is very high. Solar panels can cut this by 90%.");
        } else if (elec > 50) {
            recs.add("Switch to LED bulbs and unplug idle devices — reduces electricity usage by 30%.");
        } else if (elec > 20) {
            recs.add("Good electricity usage! Use natural light during daytime to save even more.");
        }

        // ── Rule 3: Food
        if ("nonveg".equalsIgnoreCase(food)) {
            recs.add("Non-veg diet adds 7.2 kg CO2/day. Switching to veg even 3 days/week reduces food emissions by 40%.");
        } else if ("veg".equalsIgnoreCase(food)) {
            recs.add("Good choice with vegetarian diet! Going vegan can further cut food emissions by 50%.");
        } else {
            recs.add("Excellent! Vegan diet has the lowest carbon footprint. You are leading by example!");
        }

        // ── Rule 4: Overall emission
        if (emission >= 60) {
            recs.add("CRITICAL: " + emission + " kg CO2 today. Implement all tips above immediately.");
        } else if (emission >= 30) {
            recs.add("Emission is above average. Small daily changes add up — try one new tip each week.");
        } else if (emission >= 10) {
            recs.add("You are doing okay! Aim below 10 kg CO2/day for an excellent eco score.");
        } else {
            recs.add("Outstanding! Below 10 kg CO2 today. Keep it up and inspire others!");
        }

        // ── Rule 5: 7-day trend using Streams
        if (history.size() >= 3) {
            double avg = history.stream()
                    .limit(7)
                    .mapToDouble(CarbonData::getTotalEmission)
                    .average()
                    .orElse(0);
            avg = Math.round(avg * 100.0) / 100.0;

            if (emission > avg) {
                recs.add("Today (" + emission + " kg) is above your 7-day average (" + avg + " kg). Try to reduce tomorrow.");
            } else {
                recs.add("Great progress! Today (" + emission + " kg) is below your 7-day average (" + avg + " kg).");
            }
        }

        return recs;
    }
}
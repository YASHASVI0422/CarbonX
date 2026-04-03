package com.carbonx.service;

import com.carbonx.dao.CarbonDAO;
import com.carbonx.model.CarbonData;
import com.carbonx.util.CarbonCalculator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CarbonService {

    private final CarbonDAO carbonDAO = new CarbonDAO();

    // Submit new carbon reading
    public double submitEntry(int userId, double travel, double electricity, String food) {
        CarbonData data = new CarbonData(userId, travel, electricity, food);
        CarbonCalculator.calculate(data);  // uses Lambda internally
        carbonDAO.save(data);
        System.out.println("Entry saved. Emission: " + data.getTotalEmission() + " kg CO2");
        return data.getTotalEmission();
    }

    // Get all entries for this user
    public List<CarbonData> getHistory(int userId) {
        return carbonDAO.getByUserId(userId);
    }

    // STREAM: sum of all emissions
    public double getTotalEmission(int userId) {
        return carbonDAO.getByUserId(userId)
                .stream()
                .mapToDouble(CarbonData::getTotalEmission)
                .sum();
    }

    // STREAM: group emissions by date for chart
    public Map<String, Double> getDailyEmissions(int userId) {
        return carbonDAO.getByUserId(userId)
                .stream()
                .collect(Collectors.groupingBy(
                        cd -> cd.getDate().toString(),
                        Collectors.summingDouble(CarbonData::getTotalEmission)
                ));
    }

    // STREAM: find single highest emission day
    public CarbonData getHighestEmissionEntry(int userId) {
        return carbonDAO.getByUserId(userId)
                .stream()
                .max(Comparator.comparingDouble(CarbonData::getTotalEmission))
                .orElse(null);
    }
}
package com.carbonx.util;

import com.carbonx.model.CarbonData;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CarbonCalculator {

    // Lambda BiFunction: multiplies value by emission factor
    private static final BiFunction<Double, Double, Double> emissionFactor =
            (value, factor) -> value * factor;

    // Lambda Function: returns food emission based on diet type
    private static final Function<String, Double> foodFactor = food -> {
        switch (food.toLowerCase()) {
            case "vegan":  return 1.5;
            case "veg":    return 3.0;
            case "nonveg": return 7.2;
            default:       return 3.0;
        }
    };

    // Main calculation method
    // Formula: (travel * 0.21) + (electricity * 0.85) + foodFactor
    public static double calculate(CarbonData data) {
        double travelEmission      = emissionFactor.apply(data.getTravel(), 0.21);
        double electricityEmission = emissionFactor.apply(data.getElectricity(), 0.85);
        double foodEmission        = foodFactor.apply(data.getFood());

        double total = travelEmission + electricityEmission + foodEmission;
        total = Math.round(total * 100.0) / 100.0; // round to 2 decimal places
        data.setTotalEmission(total);
        return total;
    }

    // Eco score: higher is better (greener user)
    public static double calculateEcoScore(double totalEmission) {
        return Math.max(0, Math.round((100 - totalEmission) * 100.0) / 100.0);
    }
}
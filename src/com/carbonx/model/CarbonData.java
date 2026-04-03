package com.carbonx.model;

import java.io.Serializable;
import java.time.LocalDate;

public class CarbonData implements Serializable {

    private static final long serialVersionUID = 2L;

    private int       id;
    private int       userId;
    private double    travel;       // in km
    private double    electricity;  // in units
    private String    food;         // veg / nonveg / vegan
    private double    totalEmission;
    private LocalDate date;

    // Default constructor
    public CarbonData() {}

    // Parameterized constructor
    public CarbonData(int userId, double travel, double electricity, String food) {
        this.userId      = userId;
        this.travel      = travel;
        this.electricity = electricity;
        this.food        = food;
        this.date        = LocalDate.now();
    }

    // Getters and Setters
    public int    getId()           { return id; }
    public void   setId(int id)     { this.id = id; }

    public int    getUserId()             { return userId; }
    public void   setUserId(int userId)   { this.userId = userId; }

    public double getTravel()               { return travel; }
    public void   setTravel(double travel)  { this.travel = travel; }

    public double getElectricity()                    { return electricity; }
    public void   setElectricity(double electricity)  { this.electricity = electricity; }

    public String getFood()             { return food; }
    public void   setFood(String food)  { this.food = food; }

    public double getTotalEmission()                      { return totalEmission; }
    public void   setTotalEmission(double totalEmission)  { this.totalEmission = totalEmission; }

    public LocalDate getDate()              { return date; }
    public void      setDate(LocalDate date){ this.date = date; }

    @Override
    public String toString() {
        return "CarbonData [userId=" + userId + ", travel=" + travel +
               ", electricity=" + electricity + ", food=" + food +
               ", total=" + totalEmission + ", date=" + date + "]";
    }
}
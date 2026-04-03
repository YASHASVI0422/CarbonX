package com.carbonx.model;

public class LeaderboardEntry {

    private int    userId;
    private String userName;
    private String city;
    private double ecoScore;
    private double totalEmission;
    private int    rank;
    private String badge;

    public LeaderboardEntry() {}

    public LeaderboardEntry(int userId, String userName, String city,
                             double ecoScore, double totalEmission) {
        this.userId        = userId;
        this.userName      = userName;
        this.city          = city;
        this.ecoScore      = ecoScore;
        this.totalEmission = totalEmission;
        this.badge         = assignBadge(ecoScore);
    }

    private String assignBadge(double score) {
        if (score >= 80) return "Eco Hero";
        if (score >= 60) return "Green";
        if (score >= 40) return "Average";
        return "High Emitter";
    }

    // Getters and Setters
    public int    getUserId()                  { return userId; }
    public void   setUserId(int userId)        { this.userId = userId; }

    public String getUserName()                { return userName; }
    public void   setUserName(String n)        { this.userName = n; }

    public String getCity()                    { return city; }
    public void   setCity(String city)         { this.city = city; }

    public double getEcoScore()                { return ecoScore; }
    public void   setEcoScore(double e)        { this.ecoScore = e; }

    public double getTotalEmission()           { return totalEmission; }
    public void   setTotalEmission(double t)   { this.totalEmission = t; }

    public int    getRank()                    { return rank; }
    public void   setRank(int rank)            { this.rank = rank; }

    public String getBadge()                   { return badge; }
    public void   setBadge(String badge)       { this.badge = badge; }
}
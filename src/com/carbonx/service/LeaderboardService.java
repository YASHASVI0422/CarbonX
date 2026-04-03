package com.carbonx.service;

import com.carbonx.dao.LeaderboardDAO;
import com.carbonx.model.LeaderboardEntry;
import com.carbonx.util.CarbonCalculator;

import java.util.List;

public class LeaderboardService {

    private final LeaderboardDAO leaderboardDAO = new LeaderboardDAO();

    // Update score for a user after new carbon entry
    public void updateScore(int userId, double totalEmission) {
        double ecoScore = CarbonCalculator.calculateEcoScore(totalEmission);
        leaderboardDAO.saveScore(userId, ecoScore, totalEmission);
    }

    // Get full ranked leaderboard
    public List<LeaderboardEntry> getLeaderboard() {
        return leaderboardDAO.getLeaderboard();
    }

    // Get rank of specific user
    public int getUserRank(int userId) {
        return leaderboardDAO.getUserRank(userId);
    }
}
package com.kombat.dto;

public class ConfigDTO {
    private int spawnCost;
    private int hexPurchaseCost;
    private int initBudget;
    private int turnBudget;
    private int maxBudget;
    private int interestPct;
    private int maxTurns;
    private int maxSpawns;

    // Getters and Setters
    public int getSpawnCost() { return spawnCost; }
    public void setSpawnCost(int spawnCost) { this.spawnCost = spawnCost; }

    public int getHexPurchaseCost() { return hexPurchaseCost; }
    public void setHexPurchaseCost(int hexPurchaseCost) { this.hexPurchaseCost = hexPurchaseCost; }

    public int getInitBudget() { return initBudget; }
    public void setInitBudget(int initBudget) { this.initBudget = initBudget; }

    public int getTurnBudget() { return turnBudget; }
    public void setTurnBudget(int turnBudget) { this.turnBudget = turnBudget; }

    public int getMaxBudget() { return maxBudget; }
    public void setMaxBudget(int maxBudget) { this.maxBudget = maxBudget; }

    public int getInterestPct() { return interestPct; }
    public void setInterestPct(int interestPct) { this.interestPct = interestPct; }

    public int getMaxTurns() { return maxTurns; }
    public void setMaxTurns(int maxTurns) { this.maxTurns = maxTurns; }

    public int getMaxSpawns() { return maxSpawns; }
    public void setMaxSpawns(int maxSpawns) { this.maxSpawns = maxSpawns; }
}

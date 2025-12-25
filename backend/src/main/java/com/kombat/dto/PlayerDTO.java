package com.kombat.dto;

public class PlayerDTO {
    private String name;
    private String shortName;
    private int budget;
    private int ownedHexCount;
    private int minionCount;
    private int totalSpawns;
    private boolean canSpawn;
    private boolean isBot;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public int getBudget() { return budget; }
    public void setBudget(int budget) { this.budget = budget; }

    public int getOwnedHexCount() { return ownedHexCount; }
    public void setOwnedHexCount(int ownedHexCount) { this.ownedHexCount = ownedHexCount; }

    public int getMinionCount() { return minionCount; }
    public void setMinionCount(int minionCount) { this.minionCount = minionCount; }

    public int getTotalSpawns() { return totalSpawns; }
    public void setTotalSpawns(int totalSpawns) { this.totalSpawns = totalSpawns; }

    public boolean isCanSpawn() { return canSpawn; }
    public void setCanSpawn(boolean canSpawn) { this.canSpawn = canSpawn; }

    public boolean isBot() { return isBot; }
    public void setBot(boolean bot) { isBot = bot; }
    public void setIsBot(boolean isBot) {
        this.setBot(isBot);
    }
}
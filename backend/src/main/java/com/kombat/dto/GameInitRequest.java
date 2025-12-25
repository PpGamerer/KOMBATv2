package com.kombat.dto;

import java.util.List;

public class GameInitRequest {
    private String gameMode; // "DUEL", "SOLITAIRE", "AUTO"
    private List<MinionConfig> minionConfigs;
    private boolean withFreeSpawn;

    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    // Note: Ensure GameMode enum is accessible or imported here
    public GameMode getGameModeEnum() { return GameMode.valueOf(gameMode.toUpperCase()); }

    public List<MinionConfig> getMinionConfigs() { return minionConfigs; }
    public void setMinionConfigs(List<MinionConfig> minionConfigs) { this.minionConfigs = minionConfigs; }

    public boolean isWithFreeSpawn() { return withFreeSpawn; }
    public void setWithFreeSpawn(boolean withFreeSpawn) { this.withFreeSpawn = withFreeSpawn; }
}
package com.kombat.dto;

import java.util.List;

public class GameStateDTO {
    private int turnCounter;
    private String currentPlayerName;
    private String gameMode;
    private List<PlayerDTO> players;
    private List<HexTileDTO> board;
    private List<String> availableMinionTypes;
    private List<String> gameLog;

    public int getTurnCounter() { return turnCounter; }
    public void setTurnCounter(int turnCounter) { this.turnCounter = turnCounter; }

    public String getCurrentPlayerName() { return currentPlayerName; }
    public void setCurrentPlayerName(String currentPlayerName) { this.currentPlayerName = currentPlayerName; }

    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    public List<PlayerDTO> getPlayers() { return players; }
    public void setPlayers(List<PlayerDTO> players) { this.players = players; }

    public List<HexTileDTO> getBoard() { return board; }
    public void setBoard(List<HexTileDTO> board) { this.board = board; }

    public List<String> getAvailableMinionTypes() { return availableMinionTypes; }
    public void setAvailableMinionTypes(List<String> availableMinionTypes) { this.availableMinionTypes = availableMinionTypes; }

    public List<String> getGameLog() { return gameLog; }
    public void setGameLog(List<String> gameLog) { this.gameLog = gameLog; }
}
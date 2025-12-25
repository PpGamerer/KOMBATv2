package com.kombat.dto;

public class GameInitResponse {
    private boolean success;
    private String message;
    private GameStateDTO gameState;

    public GameInitResponse(boolean success, String message, GameStateDTO gameState) {
        this.success = success;
        this.message = message;
        this.gameState = gameState;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public GameStateDTO getGameState() { return gameState; }
    public void setGameState(GameStateDTO gameState) { this.gameState = gameState; }
}
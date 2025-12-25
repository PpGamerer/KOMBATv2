package com.kombat.dto;

public class TurnResponse {
    private boolean success;
    private String message;
    private GameStateDTO gameState;
    private boolean gameOver;

    public TurnResponse(boolean success, String message, GameStateDTO gameState, boolean gameOver) {
        this.success = success;
        this.message = message;
        this.gameState = gameState;
        this.gameOver = gameOver;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public GameStateDTO getGameState() { return gameState; }
    public void setGameState(GameStateDTO gameState) { this.gameState = gameState; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
}
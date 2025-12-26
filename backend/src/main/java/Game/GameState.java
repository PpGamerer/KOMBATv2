package Game;

import Board.HexGrid;
import Board.HexTile;
import Minion.Minion;
import Player.Player;

import java.util.*;

public class GameState {
    // Static Fields
    private static GameState instance;
    public static HexGrid board;
    public static List<Player> players = new ArrayList<>();
    public static Player currentPlayer;
    public static Minion currentMinion;
    public static int turnCounter = 1;
    public static int maxTurns;

    public static void reset() {
        instance = null;
        board = null;
        players.clear();
        currentPlayer = null;
        currentMinion = null;
        turnCounter = 1;
    }

    // Singleton Pattern
    public static GameState getInstance(HexGrid board) {
        if (instance == null) {
            instance = new GameState(board);
        } else {
            GameState.board = board;
        }
        return instance;
    }

    // Constructor
    public GameState(HexGrid board) {
        GameState.board = board;
        ConfigLoader.loadConfig("backend/config.txt");
        maxTurns = ConfigLoader.get("max_turns");
        turnCounter = 1;
    }

    private GameState() {}

    // ================== LEGACY GETTERS (แก้ Error: cannot find symbol) ==================
    // เพิ่ม Method เหล่านี้กลับมาเพื่อให้ Code เก่า (Player/Bot/Parser) เรียกใช้ได้

    public static HexGrid getBoard() {
        return board;
    }

    public static Minion getCurrentMinion() {
        return currentMinion;
    }

    // ================== METHODS FOR SERVICE LAYER ==================

    public int getTurnCounter() {
        return turnCounter;
    }

    public void incrementTurnCounter() {
        turnCounter++;
    }

    public boolean isGameOver() {
        if (turnCounter > maxTurns) return true;

        int playersWithMinions = 0;
        for (Player player : players) {
            if (!player.getMinions().isEmpty()) {
                playersWithMinions++;
            }
        }
        return turnCounter > 2 && playersWithMinions <= 1;
    }

    public String getWinnerName() {
        if (!isGameOver()) return null;

        Player winner = null;
        int playersWithMinions = 0;

        for (Player player : players) {
            if (!player.getMinions().isEmpty()) {
                winner = player;
                playersWithMinions++;
            }
        }

        if (playersWithMinions > 1 || winner == null) {
            winner = players.stream()
                    .max(Comparator.comparingDouble(Player::getBudget))
                    .orElse(null);
        }

        return (winner != null) ? winner.getName() : "Draw";
    }

    public void executeMinionStrategies(Player player) {
        List<Minion> minions = new ArrayList<>(player.getMinions());
        for (Minion minion : minions) {
            HexTile tile = board.getTile(minion.getRow(), minion.getCol());
            if (tile.getMinion() != minion) continue;

            currentMinion = minion;
            updateSpecialVariables();

            try {
                minion.executeStrategy();
            } catch (Exception e) {
                System.out.println("Strategy Error (" + minion.getName() + "): " + e.getMessage());
            }
        }
    }

    // ================== PARSER CONTEXT UPDATER ==================

    public static void updateSpecialVariables() {
        if (currentMinion == null || currentPlayer == null) return;

        try {
            Map<String, Object> context = currentPlayer.getContext();

            if (context != null) {
                context.put("row", (long) currentMinion.getRow());
                context.put("col", (long) currentMinion.getCol());
                context.put("random", (long) (Math.random() * 1000));
                context.put("budget", (long) currentPlayer.getBudget());

                double baseInterestRate = ConfigLoader.get("interest_pct");
                double currentBudget = currentPlayer.getBudget();
                double dynamicInterestRate = baseInterestRate * Math.log10(currentBudget > 0 ? currentBudget : 1) * Math.log(turnCounter > 0 ? turnCounter : 1);
                double interest = currentBudget * (dynamicInterestRate / 100.0);
                context.put("int", (long) interest);

                context.put("maxbudget", (long) ConfigLoader.get("max_budget"));

                long maxSpawns = ConfigLoader.get("max_spawns");
                context.put("spawnsleft", maxSpawns - currentPlayer.getTotalSpawns());
            }
        } catch (Exception e) {
            // ignore errors
        }
    }
}
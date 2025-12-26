package Player;

import Board.HexGrid;
import Board.HexTile;
import Game.ConfigLoader;
import Game.GameState;
import Interfaces.PlayerInterface;
import Minion.Minion;
import Minion.MinionType;
import Minion.MinionFactory;

import java.util.*;

public class Player implements PlayerInterface {
    private final String name;
    public double budget;
    public final Set<HexTile> ownedHexes;
    private Queue<Minion> minions;
    private final Map<String, Object> context;
    private boolean hasSpawnedThisTurn;
    private HexGrid board;
    private int totalSpawns = 0;

    // Constructor
    public Player(String name) {
        this.name = name;
        this.budget = ConfigLoader.get("init_budget");  // Read initial budget from config
        this.ownedHexes = new HashSet<>();
        this.minions = new LinkedList<>();
        this.context = new HashMap<>();
        this.context.put("budget", this.budget);
        this.board = GameState.getBoard();
        this.hasSpawnedThisTurn = false;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getShortName() {
        if(Objects.equals(this.getName(), "Player1")) return "P1";
        if(Objects.equals(this.getName(), "Player2")) return "P2";
        if(Objects.equals(this.getName(), "Bot1")) return "B1";
        if(Objects.equals(this.getName(), "Bot2")) return "B2";
        return "";
    }

    public double getBudget() {
        return this.budget;
    }

    public int getTotalSpawns(){
        return this.totalSpawns;
    }

    public void incrementTotalSpawns(){
        this.totalSpawns++;
    }

    // Adjust budget after an action
    public void adjustBudget(double amount) {
        this.budget = Math.min(this.budget + amount, ConfigLoader.get("max_budget")); // Max budget constraint
        this.context.put("budget", (long) this.budget);
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void startTurn() {
        hasSpawnedThisTurn = false;
    }

    public boolean canSpawn() {
        return !hasSpawnedThisTurn;
    }

    // Spawn a Minion on a HexTile
    public Minion spawnMinion(HexTile hex, MinionType minionType, boolean isFreeSpawn) {
        int spawnCost = ConfigLoader.get("spawn_cost");

        if (hasSpawnedThisTurn) {
            System.out.println("You can only spawn one minion per turn.");
        }

        if (!isFreeSpawn && budget < spawnCost) {
            System.out.println("Not enough budget to spawn " + minionType.getCustomName());
            return null;
        }

        if(hex.getOwner() != this){
            System.out.println("Cannot spawn on this hex. It's not your hex.");
            return null;
        }
        if (hex.isOccupied()) {
            System.out.println("Cannot spawn on this hex. It's already occupied.");
            return null;
        }

        Minion minion = MinionFactory.createMinion(minionType, hex, this);
        if (!isFreeSpawn) {
            budget -= spawnCost;  // Deduct budget only for non-free spawns
        }
        hasSpawnedThisTurn = true;
        System.out.println("Spawned " + minionType.getCustomName() + " at " + hex.getCoordinate());

        return minion;
    }

    public void initilizeHex(){
        if (this.getName().equals("Player1") || this.getName().equals("Bot1")) {
            // Top-left 5 hexes for Player 1
            for(int i=0; i<3; i++) {
                HexTile hex = board.getTile(0, i);
                ownedHexes.add(hex);
                hex.owner = this;
            }
            for(int i=0; i<2; i++) {
                HexTile hex = board.getTile(1, i);
                ownedHexes.add(hex);
                hex.owner = this;
            }
        } else {
            // Bottom-right 5 hexes for Player 2
            for(int i=6; i<8; i++) {
                HexTile hex = board.getTile(6, i);
                ownedHexes.add(hex);
                hex.owner = this;
            }
            for(int i=5; i<8; i++) {
                HexTile hex = board.getTile(7, i);
                ownedHexes.add(hex);
                hex.owner = this;
            }
        }
    }

    // Add an owned HexTile to the player's set of owned HexTiles
    public void purchaseHex(HexTile hex) {
        int hexCost = ConfigLoader.get("hex_purchase_cost");

        if (budget < hexCost) {
            System.out.println("Not enough budget to purchase " + hex.getCoordinate());
            return;
        }

        if(!hex.isBought()) {
            budget -= hexCost;
            ownedHexes.add(hex);
            hex.owner = this;
        } else if (hex.isOccupied()) {
            System.out.println("This hex is already occupied. by " + hex.getOwner());
        }
    }

    // Get the list of Minions owned by this player
    public Queue<Minion> getMinions() {
        return minions;
    }

    public Set<HexTile> getOwnedHexes() {
        return ownedHexes;
    }

    public void setHasSpawned(boolean b) {
        hasSpawnedThisTurn = b;
    }
}

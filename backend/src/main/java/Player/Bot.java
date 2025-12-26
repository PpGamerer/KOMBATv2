package Player;

import Board.HexTile;
import Game.GameSetup;
import Game.GameState;
import Interfaces.PlayerInterface;
import Minion.MinionType;
import Game.ConfigLoader;

import java.util.List;
import java.util.Random;

public class Bot extends Player implements PlayerInterface {
    String name;
    private final Random random;

    public Bot(String name) {
        super(name);
        this.name = name;
        this.random = new Random();
    }

    public void purchaseRandomHex() {
        int hexCost = ConfigLoader.get("hex_purchase_cost");
        List<HexTile> adjacentHexes = GameState.getBoard().getAdjacentHexes(getOwnedHexes());
        if (adjacentHexes.isEmpty()) {
            System.out.println("No adjacent hexes available for purchase.");
            return;
        }

        if (!adjacentHexes.isEmpty()) {
            HexTile chosenHex = adjacentHexes.get(random.nextInt(adjacentHexes.size()));
            if (this.getBudget() >= hexCost) {
                purchaseHex(chosenHex);
                System.out.println(name + " (Bot) purchased hex at " + chosenHex.getCoordinate());
            } else {
                System.out.println("Not enough budget to purchase this hex.");
            }
        }
    }

    public void spawnRandomMinion(boolean Isfree) {
        int spawnCost = ConfigLoader.get("spawn_cost");
        int maxSpawn = ConfigLoader.get("max_spawns"); // Load max_spawns from config
        int totalSpawned = this.getTotalSpawns(); // Track spawned minions

        if (!Isfree && this.getBudget() < spawnCost) {
            System.out.println(this.getName() + " does not have enough budget to spawn a minion.");
            return;
        }

        if (totalSpawned > maxSpawn) {
            System.out.println(this.getName() + " has reached the max spawn limit (" + maxSpawn + ") for this turn.");
            return;
        }

        boolean hasAvailableHex = false;
        for (HexTile hex : getOwnedHexes()) {
            if (!hex.isOccupied()) {
                hasAvailableHex = true;
                MinionType chosenMinion = GameSetup.minionTypes.get(random.nextInt(GameSetup.minionTypes.size()));
                spawnMinion(hex, chosenMinion, Isfree);
                System.out.println(name + " (Bot) spawned " + chosenMinion.getCustomName() + " at " + hex.getCoordinate());
                this.setHasSpawned(true);
                this.incrementTotalSpawns();
                break;
            }
        }

        if (!hasAvailableHex) {
                System.out.println("No available hex.");
                return;
        }
    }
}

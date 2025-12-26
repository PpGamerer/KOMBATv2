package Parser.AST;

import Board.Direction;
import Board.HexTile;
import Game.GameState;
import Interfaces.Evaluator;
import Minion.Minion;
import Player.Player;

import java.util.Map;

public class MoveCommand extends ActionCommand implements Evaluator {
    private final String direction;

    public MoveCommand(String direction) {
        this.direction = direction;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        Minion currentMinion = GameState.getCurrentMinion();
        Player player = currentMinion.getOwner();
        if (currentMinion == null) {
            throw new RuntimeException("MoveCommand: No currentMinion found.");
        }

        HexTile currentTile = currentMinion.getCurrentTile();
        Direction moveDir = Direction.fromString(direction);

        if (moveDir == null) {
            System.out.println("🚨 MoveCommand: Invalid direction -> " + direction);
            return 0;
        }

        HexTile targetTile = currentTile.getValidMoveTile(moveDir);

        if (targetTile == null) {
            //System.out.println("🚨 Invalid move: No neighbor in direction " + moveDir);
            return 0;
        }

        if (targetTile.isOccupied()) {
            //System.out.println(currentMinion.getName() + " cannot move to " + targetTile.getCoordinate() + " (Occupied). No-op.");
            return 0;
        }

        // Deduct budget and move minion
        if (player.getBudget() < 1) {
            System.out.println("MoveCommand: Not enough budget to move.");
            return -1; // End the turn
        }
        player.adjustBudget(-1);

        System.out.println(currentMinion.getName() + " moved from " + currentTile.getCoordinate() + " to " + targetTile.getCoordinate());
        currentMinion.move(targetTile);

        return 0;
    }

    @Override
    public String toString() {
        return "move " + direction;
    }
}

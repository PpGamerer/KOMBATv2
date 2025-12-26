package Parser.AST;

import Board.Direction;
import Board.HexGrid;
import Board.HexTile;
import Game.GameState;
import Interfaces.Evaluator;
import Minion.Minion;
import Player.Player;

import java.util.Map;

public class AttackCommand extends ActionCommand implements Evaluator {
    private final String direction;
    private final Expression power;

    public AttackCommand(String direction, Expression power) {
        this.direction = direction;
        this.power = power;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        Minion currentMinion = GameState.getCurrentMinion();
        HexGrid board = GameState.getBoard();
        Player player = currentMinion.getOwner();

        // Evaluate power expenditure
        long expenditure = power.evaluate(context);
        long totalCost = expenditure + 1;

        if (player.getBudget() < totalCost) {
            System.out.println("Not enough budget to attack.");
            return 0;  // No-op if budget is insufficient
        }

        Direction dir = Direction.fromString(direction);
        if (dir == null) {
            System.out.println("🚨 Invalid direction: " + direction);  // Debugging: invalid direction
            return 0;
        }

        // Log direction processing
        //System.out.println("🔍 Parsing direction for attack: " + direction);

        // Find the target hex based on direction
        HexTile targetTile = currentMinion.getCurrentTile().getNeighbor(dir);
        if (targetTile == null || !board.isValidPosition(targetTile.getRow(), targetTile.getCol())) {
            System.out.println("🚨 No valid target in direction: " + direction);
            return 0;
        }

        // Deduct the total cost from the player's budget
        player.adjustBudget((int) -totalCost);

        if (!targetTile.isOccupied()) {
            System.out.println("Target hex is empty. Attack cost applied, but no effect.");
            return expenditure;  // Cost is applied even if the target is empty
        }

        // If target is occupied, apply damage
        Minion targetMinion = targetTile.getMinion();
        int effectiveDamage = Math.max(1, (int) (expenditure - targetMinion.getDefense()));
        boolean defeated = targetMinion.receiveDamage(effectiveDamage);  // Call to apply damage

        // Log attack and result
        //System.out.println("🔨 Attacked " + targetMinion.getName() + " for " + damage + " damage.");
        if (defeated) {
            System.out.println(targetMinion.getName() + " has been defeated!");
            targetTile.removeMinion();  // Remove minion from hex if its health drops to 0
            targetMinion.getOwner().getMinions().remove(targetMinion); // Remove minion from player if its health drops to 0
        }

        return expenditure;  // Return the expenditure used for the attack
    }

    @Override
    public String toString() {
        return "shoot " + direction + " with power " + power.toString();
    }
}

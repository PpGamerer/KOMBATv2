package Parser.AST;

import Board.Direction;
import Board.HexGrid;
import Board.HexTile;
import Game.GameState;
import Minion.Minion;

import java.util.Map;

public class InfoExpression extends Expression {
    private final String type;
    private final String direction;

    public InfoExpression(String type, String direction) {
        this.type = type;
        this.direction = direction;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        HexGrid board = GameState.getBoard();
        Minion currentMinion = GameState.getCurrentMinion();

        switch (type) {
            case "opponent":
                return findClosestOpponent(currentMinion);
            case "ally":
                return findClosestAlly(currentMinion);
            case "nearby":
                Direction dir = Direction.fromString(direction);
                if (dir == null) {
                    System.out.println("Invalid direction: " + direction);
                    return 0;
                }
                return calculateNearbyValue(currentMinion, dir);
            default:
                throw new RuntimeException("Unknown info expression: " + type);
        }
    }

    private long findClosestOpponent(Minion currentMinion) {
        long minValue = Long.MAX_VALUE;

        for (Direction dir : Direction.values()) {
            int distance = 1;
            HexTile currentTile = currentMinion.getCurrentTile().getNeighbor(dir);

            while (currentTile != null) {
                if (currentTile.isOccupied()) {
                    Minion foundMinion = currentTile.getMinion();
                    if (foundMinion.getOwner() != currentMinion.getOwner()) {
                        long value = distance * 10 + (dir.ordinal() + 1);
                        minValue = Math.min(minValue, value);
                        break;
                    }
                }
                currentTile = currentTile.getNeighbor(dir);
                distance++;
            }
        }
        return minValue == Long.MAX_VALUE ? 0 : minValue;
    }

    private long findClosestAlly(Minion currentMinion) {
        long minValue = Long.MAX_VALUE;

        for (Direction dir : Direction.values()) {
            int distance = 1;
            HexTile currentTile = currentMinion.getCurrentTile().getNeighbor(dir);

            while (currentTile != null) {
                if (currentTile.isOccupied()) {
                    Minion foundMinion = currentTile.getMinion();
                    if (foundMinion.getOwner() == currentMinion.getOwner()) {
                        long value = distance * 10 + (dir.ordinal() + 1);
                        minValue = Math.min(minValue, value);
                        break;
                    }
                }
                currentTile = currentTile.getNeighbor(dir);
                distance++;
            }
        }
        return minValue == Long.MAX_VALUE ? 0 : minValue;
    }

    private long calculateNearbyValue(Minion currentMinion, Direction direction) {
        int distance = 1;
        HexTile currentTile = currentMinion.getCurrentTile().getNeighbor(direction);

        while (currentTile != null) {
            if (currentTile.isOccupied()) {
                Minion foundMinion = currentTile.getMinion();
                int hpDigits = String.valueOf(foundMinion.getHealth()).length();
                int defenseDigits = String.valueOf(foundMinion.getDefense()).length();
                long value = 100 * hpDigits + 10 * defenseDigits + distance;

                return (foundMinion.getOwner() == currentMinion.getOwner()) ? -value : value;
            }
            currentTile = currentTile.getNeighbor(direction);
            distance++;
        }
        return 0;
    }

    @Override
    public String toString() {
        return type + " " + direction;
    }
}

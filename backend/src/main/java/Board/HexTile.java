package Board;

import Minion.Minion;
import Player.Player;

import java.util.*;

public class HexTile {
    private final int row;
    private final int col;
    private Minion minion;  // Stores the name of the minion occupying the tile
    public Player owner;   // The owner of the tile, if set
    private Map<Direction, HexTile> neighbors = new HashMap<>();

    public HexTile(int row, int col) {
        this.row = row;
        this.col = col;
        this.minion = null;
        this.owner = null;
    }

    public void reset() {
        this.owner = null;
        this.minion = null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void addNeighbor(Direction direction, HexTile neighbor) {
        if (neighbor == null) {
            System.out.println("⚠ Warning: Attempted to add NULL neighbor for " + this.getCoordinate() + " in direction " + direction);
        } else {
            neighbors.put(direction, neighbor);
        }
    }

    public HexTile getNeighbor(Direction direction) {
        HexTile neighbor = neighbors.get(direction);
        if (neighbor == null) {
            //System.out.println("No neighbor found in direction " + direction + " from " + this.getCoordinate());
        } else {
            //System.out.println("Found neighbor " + neighbor.getCoordinate() + " in direction " + direction);
        }
        return neighbor;
    }

    public boolean isOccupied() {
        return minion != null;
    }

    public boolean isBought() {
        return owner != null;
    }

    public Minion getMinion() {
        return minion;
    }

    public void setMinion(Minion minion) {
        this.minion = minion;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void moveMinion(Minion minion) {
        if(isOccupied()) {
            throw new IllegalStateException("Tile is already occupied by another minion.");
        }
        this.minion = minion;
    }

    public void removeMinion() {
        this.minion = null;
    }

    public String getCoordinate() {
        String ownerMark = (owner != null) ? "{" + owner.getShortName() + "}" : "{-}";
        String minionMark = (minion != null) ? "[" + minion.getName() + "|" + minion.getOwner().getShortName() + "]" + ownerMark : "[-]" + ownerMark;
        return "(" + (row + 1) + "," + (col + 1) + ")" + minionMark;
    }

    public String getHexContent() {
        String ownerMark = (owner != null) ? "{" + owner.getShortName() + "}" : "{-}";
        return (minion != null) ? "[" + minion.getName() + "|" + minion.getOwner().getShortName() + "]" + ownerMark : "[-]" + ownerMark;
    }


    public HexTile getValidMoveTile(Direction direction) {
        HexTile targetTile = getNeighbor(direction);

        if (targetTile == null) {
            System.out.println("Invalid move: No neighbor in direction " + direction);
            return null;
        }

        if (targetTile.isOccupied()) {
            System.out.println("Invalid move: Target tile " + targetTile.getCoordinate() + " is already occupied.");
            return null;
        }

        return targetTile;  // Valid move
    }
}
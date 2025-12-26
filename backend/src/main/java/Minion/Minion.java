package Minion;

import Board.HexTile;
import Game.ConfigLoader;
import Parser.AST.Statement;
import Parser.DoneException;
import Player.Player;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Minion {
    private final MinionType type;
    private int health;
    private int defense;
    private final Player owner;
    private final List<Statement> strategy;
    private Map<String, Object> context;
    private int row, col;
    private HexTile currentTile;

    public Minion(MinionType type, Player owner) {
        this.type = type;
        this.health = ConfigLoader.get("init_hp");
        this.owner = owner;
        this.defense = type.getDefenseFactor();
        this.strategy = type.getStrategy();
        this.context = owner.getContext(); // Use player's context (budget)
        this.row = 0;  // Set initial position (customize as needed)
        this.col = 0;
        this.currentTile = null;
    }

    public MinionType getType() {
        return this.type;
    }

    public HexTile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(HexTile Tile){
        currentTile = Tile;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getName() {
        return type.getCustomName();
    }

    public int getHealth() {
        return health;
    }

    public int getDefense() {
        return defense;
    }

    public Player getOwner() {
        return owner;
    }

    public List<Statement> getStrategy() {
        return strategy;
    }

    public void executeStrategy() {
        try {
        if(health!=0) {
            this.context = owner.getContext();
            System.out.println(type.getCustomName() + " executing strategy...");
            for (Statement stmt : type.getStrategy()) {
                stmt.evaluate(context);
            }
        }
        } catch (DoneException e) {
            System.out.println("Execution stopped due to DoneCommand.");
        }
    }

    public boolean move(HexTile targetTile) {
        if (targetTile == null || targetTile.isOccupied()) {
            System.out.println(getName() + " tried to move to an invalid or occupied tile.");
            return false;
        }

        currentTile.removeMinion();  // Remove minion from the current tile
        setCurrentTile(targetTile);  // Update minion's current tile
        this.row = targetTile.getRow();
        this.col = targetTile.getCol();
        targetTile.moveMinion(this);
        //System.out.println(getName() + " moved to " + targetTile.getCoordinate());
        return true;
    }

    public boolean receiveDamage(int effectiveDamage) {
        this.health = Math.max(0, this.health - effectiveDamage);
        System.out.println(type.getCustomName() + " received " + effectiveDamage + " damage. Remaining HP: " + health);

        if (this.health == 0) {
            return true; // Minion defeated
        }
        return false; // Minion still alive
    }


    public void displayStatus() {
        System.out.println(type + " Status: Health = " + health + ", Defense = " + defense);
    }
}

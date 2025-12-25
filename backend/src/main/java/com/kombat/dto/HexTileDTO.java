package com.kombat.dto;

public class HexTileDTO {
    private int row;
    private int col;
    private boolean occupied;
    private boolean bought;
    private String ownerName;
    private MinionDTO minion;

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }

    public boolean isBought() { return bought; }
    public void setBought(boolean bought) { this.bought = bought; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public MinionDTO getMinion() { return minion; }
    public void setMinion(MinionDTO minion) { this.minion = minion; }
}
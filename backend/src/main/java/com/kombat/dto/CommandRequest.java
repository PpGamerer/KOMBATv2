package com.kombat.dto;

public class CommandRequest {
    private String commandType; // "BUY_HEX", "SPAWN_MINION"
    private int row;
    private int col;
    private Integer minionTypeIndex;
    private boolean isFree;

    public String getCommandType() { return commandType; }
    public void setCommandType(String commandType) { this.commandType = commandType; }

    public CommandType getCommandTypeEnum() {
        return CommandType.valueOf(commandType.toUpperCase());
    }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public Integer getMinionTypeIndex() { return minionTypeIndex; }
    public void setMinionTypeIndex(Integer minionTypeIndex) {
        this.minionTypeIndex = minionTypeIndex;
    }

    public boolean isFreeSpawn() {
        return isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }
}
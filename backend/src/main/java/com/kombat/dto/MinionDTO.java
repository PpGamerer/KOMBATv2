package com.kombat.dto;

public class MinionDTO {
    private String name;
    private int health;
    private int defense;
    private String ownerName;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
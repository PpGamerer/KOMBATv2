package Interfaces;

import Board.HexTile;
import Minion.Minion;
import Minion.MinionType;

import java.util.Queue;

public interface PlayerInterface {
    String getName();
    double getBudget();
    void adjustBudget(double amount);
    Minion spawnMinion(HexTile hex, MinionType minionType, boolean isFreeSpawn);
    void purchaseHex(HexTile hex);
    Queue<Minion> getMinions();
}
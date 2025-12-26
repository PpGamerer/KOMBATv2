package Minion;

import Board.HexTile;
import Player.Player;

public class MinionFactory {

    public static Minion createMinion(MinionType type, HexTile spawnHex, Player owner) {
        // สร้าง Minion object
        Minion minion = new Minion(type, owner);

        // เซ็ตข้อมูลตำแหน่งและ tile
        minion.setCurrentTile(spawnHex);
        minion.setRow(spawnHex.getRow());
        minion.setCol(spawnHex.getCol());

        // เซ็ต HexTile ให้รู้ว่าถูกครอบครอง
        spawnHex.setMinion(minion);
        spawnHex.setOwner(owner);

        // เพิ่ม minion เข้า player queue
        owner.getMinions().add(minion);

        System.out.println(owner.getName() + " spawned " + type.getCustomName() + " at " + spawnHex.getCoordinate());

        return minion;
    }
}

package backend;

import HexMap.HexMap;

import javax.swing.*;

class MainHexMap {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hex Map Game");
        HexMap hexMap = new HexMap();
        frame.add(hexMap);
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
package backend;

import Board.HexGrid;
import Game.GameState;

public class Main {
    public static void main(String[] args) {
        HexGrid map = HexGrid.getInstance();
        GameState game = GameState.getInstance(map);
        game.setupGame();
        game.startGame();    // 🎮 Start the Gameplay Loop
        //map.printBoard();
    }
}
package Board;

import Minion.Minion;

import java.util.*;

// HexMapGraph class representing the entire board
public class HexGrid {
    private int rows;
    private int cols;
    public HexTile[][] board;

    public static void resetInstance() {
        instance = null;
        System.out.println("🔄 HexGrid instance reset");
    }

    private static HexGrid instance;
    public static HexGrid getInstance() {
        if (instance == null) {
            instance = new HexGrid();
        }
        return instance;
    }

    public HexGrid() {
        this.rows = 8; // Fixed to 8 rows
        this.cols = 8; // Fixed to 8 columns
        board = new HexTile[this.rows][this.cols];
        initializeBoard();
        initializeNeighbors();
    }

    // Initialize the hex tiles
    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new HexTile(i, j);
            }
        }
    }

    private void initializeNeighbors() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                HexTile currentTile = board[row][col];

                // Calculate the correct grid row based on array row and column
                int gridRow = (row * 2) + ((col % 2 == 0) ? 2 : 1);
                int gridCol = col + 1;

                //System.out.println("(Array: " + row + ", " + col + "), Grid (row: " + gridRow + ", col: " + gridCol + ")");

                int[][] offsets = (gridRow % 2 == 0) ? getEvenRowOffsets() : getOddRowOffsets();
                for (int i = 0; i < offsets.length; i++) {
                    int neighborRow = row + offsets[i][0];
                    int neighborCol = col + offsets[i][1];

                    if (isValidPosition(neighborRow, neighborCol)) {
                        HexTile neighbor = getTile(neighborRow, neighborCol);
                        Direction direction = Direction.values()[i];
                        currentTile.addNeighbor(direction, neighbor);

                        //int neighborGridRow = (neighborRow * 2) + ((neighborCol % 2 == 0) ? 2 : 1);
                        //int neighborGridCol = neighborCol + 1;
                        //System.out.println("✅ Direction: " + direction + " Assigned from Grid (row: " + gridRow + ", col: " + gridCol + ") → (row: " + neighborGridRow + ", col: " + neighborGridCol + ")");
                    }
                }
            }
        }
    }

    private int[][] getEvenRowOffsets() {
        return new int[][] {
                {-1, 0}, {0, 1},   // UP, UPRIGHT
                {1, 1}, {1, 0},    // DOWNRIGHT, DOWN
                {1, -1}, {0, -1}   // DOWNLEFT, UPLEFT
        };
    }

    private int[][] getOddRowOffsets() {
        return new int[][] {
                {-1, 0}, {-1, 1},  // UP, UPRIGHT
                {0, 1}, {1, 0},   // DOWNRIGHT, DOWN
                {0, -1}, {-1, -1}    // DOWNLEFT, UPLEFT
        };
    }

    public List<HexTile> getAdjacentHexes(Set<HexTile> ownedHexes) {
        Set<HexTile> adjacentHexes = new HashSet<>();
        for (HexTile ownedHex : ownedHexes) {
            for (Direction dir : Direction.values()) {
                HexTile neighbor = ownedHex.getNeighbor(dir);
                if (neighbor != null && !neighbor.isBought()) {
                    adjacentHexes.add(neighbor);
                }
            }
        }
        return new ArrayList<>(adjacentHexes);
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public HexTile getTile(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return null;
    }

    public Minion getMinionAt(HexTile tile) {
        if (tile.isOccupied()) {
            return tile.getMinion();
        }
        return null;
    }

//    public void printBoard() {
//        System.out.println("Board:");
//        for (int i = 0; i < rows; i++) {
//            // First line of the zigzag: print "odd" columns
//            StringBuilder oddLine = new StringBuilder();
//            for (int j = 0; j < cols; j++) {
//                if (j % 2 == 1) {
//                    oddLine.append(String.format("%-25s", board[i][j].getCoordinate()));
//                } else {
//                    oddLine.append(String.format("%-25s", "")); // Maintain spacing for alignment
//                }
//            }
//            System.out.println(oddLine);
//
//            // Second line of the zigzag: print "even" columns
//            StringBuilder evenLine = new StringBuilder();
//            for (int j = 0; j < cols; j++) {
//                if (j % 2 == 0) {
//                    evenLine.append(String.format("%-25s", board[i][j].getCoordinate()));
//                } else {
//                    evenLine.append(String.format("%-25s", "")); // Maintain spacing for alignment
//                }
//            }
//            System.out.println(evenLine);
//        }
//    }

    public void printBoard() {
        System.out.print("Board:");
        System.out.println(" [Minion|Owner] {HexOwner}");

        for (int i = 0; i < rows; i++) {
            // First print the odd column hexagons
            StringBuilder oddTop = new StringBuilder();
            StringBuilder oddMid = new StringBuilder();
            StringBuilder oddBot = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                if (j % 2 == 1) { // Odd column
                    String content = board[i][j].getHexContent();
                    oddTop.append(String.format("    /------------------\\   "));
                    oddMid.append(String.format("   / %-18s \\  ", content));
                    oddBot.append("    \\------------------/   ");
                } else {
                    // Empty space for even columns
                    oddTop.append(String.format("%-20s", ""));
                    oddMid.append(String.format("%-20s", ""));
                    oddBot.append(String.format("%-20s", ""));
                }
            }

            // Print the odd columns for this row
            System.out.println(oddTop.toString());
            System.out.println(oddMid.toString());
            System.out.println(oddBot.toString());

            // Then print the even column hexagons
            StringBuilder evenTop = new StringBuilder();
            StringBuilder evenMid = new StringBuilder();
            StringBuilder evenBot = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                if (j % 2 == 0) { // Even column
                    String content = board[i][j].getHexContent();
                    evenTop.append(String.format("    /------------------\\   "));
                    evenMid.append(String.format("   / %-18s \\  ", content));
                    evenBot.append("    \\------------------/   ");
                } else {
                    // Empty space for odd columns
                    evenTop.append(String.format("%-18s", ""));
                    evenMid.append(String.format("%-18s", ""));
                    evenBot.append(String.format("%-18s", ""));
                }
            }

            // Print the even columns for this row
            System.out.println(evenTop.toString());
            System.out.println(evenMid.toString());
            System.out.println(evenBot.toString());
        }
    }


}
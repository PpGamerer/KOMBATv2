package HexMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HexMap extends JPanel {
    private static final int HEX_SIZE = 40;
    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final Color PLAYER1_COLOR = new Color(255, 182, 193);
    private static final Color PLAYER2_COLOR = new Color(135, 206, 250);
    private final Map<Point, Color> hexOwnership = new HashMap<>();
    private boolean isPlayer1Turn = true;

    public HexMap() {
        initializeStartingHexes();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickedHex = getHexAt(e.getX(), e.getY());
                if (clickedHex != null && !hexOwnership.containsKey(clickedHex)) {
                    if (canPlayerPlaceHex(clickedHex)) {
                        hexOwnership.put(clickedHex, isPlayer1Turn ? PLAYER1_COLOR : PLAYER2_COLOR);
                        isPlayer1Turn = !isPlayer1Turn;
                        repaint();
                    }
                }
            }
        });
    }

    private void initializeStartingHexes() {
        hexOwnership.put(new Point(0, 0), PLAYER1_COLOR);
        hexOwnership.put(new Point(0, 1), PLAYER1_COLOR);
        hexOwnership.put(new Point(0, 2), PLAYER1_COLOR);
        hexOwnership.put(new Point(1, 0), PLAYER1_COLOR);
        hexOwnership.put(new Point(1, 1), PLAYER1_COLOR);

        hexOwnership.put(new Point(ROWS - 1, COLS - 1), PLAYER2_COLOR);
        hexOwnership.put(new Point(ROWS - 1, COLS - 2), PLAYER2_COLOR);
        hexOwnership.put(new Point(ROWS - 1, COLS - 3), PLAYER2_COLOR);
        hexOwnership.put(new Point(ROWS - 2, COLS - 1), PLAYER2_COLOR);
        hexOwnership.put(new Point(ROWS - 2, COLS - 2), PLAYER2_COLOR);
    }

    private boolean canPlayerPlaceHex(Point clickedHex) {
        Color currentPlayerColor = isPlayer1Turn ? PLAYER1_COLOR : PLAYER2_COLOR;
        for (Point adjacentHex : getAdjacentHexes(clickedHex)) {
            if (hexOwnership.get(adjacentHex) != null && hexOwnership.get(adjacentHex).equals(currentPlayerColor)) {
                return true;
            }
        }
        return false;
    }

    private Set<Point> getAdjacentHexes(Point hex) {
        int[] dx = {-1, 1, 0, 0, -1, 1};
        int[] dy = {0, 0, -1, 1, -1, 1};
        Set<Point> adjacentHexes = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            int nx = hex.x + dx[i];
            int ny = hex.y + dy[i];
            if (nx >= 0 && nx < ROWS && ny >= 0 && ny < COLS) {
                adjacentHexes.add(new Point(nx, ny));
            }
        }
        return adjacentHexes;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int hexWidth = (int) (HEX_SIZE * 1.5 * COLS);
        int hexHeight = (int) (HEX_SIZE * Math.sqrt(3) * (ROWS + 0.5));
        int offsetX = (panelWidth - hexWidth) / 2;
        int offsetY = (panelHeight - hexHeight) / 2;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Point hexPos = new Point(row, col);
                Color hexColor = hexOwnership.getOrDefault(hexPos, Color.LIGHT_GRAY);
                drawHex(g2, row, col, hexColor, offsetX, offsetY);
            }
        }
    }

    private Point getHexAt(int mouseX, int mouseY) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int hexWidth = (int) (HEX_SIZE * 1.5 * COLS);
        int hexHeight = (int) (HEX_SIZE * Math.sqrt(3) * (ROWS + 0.5));
        int offsetX = (panelWidth - hexWidth) / 2;
        int offsetY = (panelHeight - hexHeight) / 2;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Path2D hex = createHex(row, col, offsetX, offsetY);
                if (hex.contains(mouseX, mouseY)) {
                    return new Point(row, col);
                }
            }
        }
        return null;
    }

    private Path2D createHex(int row, int col, int offsetX, int offsetY) {
        int x = offsetX + col * (int) (HEX_SIZE * 1.5);
        int y = offsetY + row * (int) (HEX_SIZE * Math.sqrt(3));
        if (col % 2 == 0) {
            y += (int) (HEX_SIZE * Math.sqrt(3) / 2);
        }

        Path2D hex = new Path2D.Double();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            int px = (int) (x + HEX_SIZE * Math.cos(angle));
            int py = (int) (y + HEX_SIZE * Math.sin(angle));
            if (i == 0) {
                hex.moveTo(px, py);
            } else {
                hex.lineTo(px, py);
            }
        }
        hex.closePath();
        return hex;
    }

    private void drawHex(Graphics2D g2, int row, int col, Color color, int offsetX, int offsetY) {
        Path2D hex = createHex(row, col, offsetX, offsetY);
        g2.setColor(color);
        g2.fill(hex);
        g2.setColor(Color.BLACK);
        g2.draw(hex);
    }
}
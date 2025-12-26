package Board;

public enum Direction {
    UP(1),
    UPRIGHT(2),
    DOWNRIGHT(3),
    DOWN(4),
    DOWNLEFT(5),
    UPLEFT(6);

    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Direction fromString(String dir) {
        switch (dir.toLowerCase()) {
            case "up": return UP;
            case "upright": return UPRIGHT;
            case "downright": return DOWNRIGHT;
            case "down": return DOWN;
            case "downleft": return DOWNLEFT;
            case "upleft": return UPLEFT;
            default: return null;
        }
    }
}

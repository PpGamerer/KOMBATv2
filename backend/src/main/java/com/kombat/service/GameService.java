package com.kombat.service;

import com.kombat.dto.*;
import Board.HexGrid;
import Board.HexTile;
import Game.ConfigLoader;
import Game.GameMode;
import Game.GameSetup;
import Game.GameState;
import Minion.Minion;
import Minion.MinionType;
import Parser.AST.Statement;
import Parser.Parser;
import Player.Bot;
import Player.Player;
import Tokenizer.Token;
import Tokenizer.Tokenizer;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {

    private GameState gameState;
    private List<String> gameLog;
    private HexGrid board;

    public GameService() {
        this.gameLog = new ArrayList<>();
        // สร้าง Board รอไว้เลย
        this.board = HexGrid.getInstance();
    }

    public GameStateDTO getGameState() {
        if (gameState == null) {
            return null;
        }
        return getGameStateDTO();
    }

    public GameInitResponse initializeGame(GameInitRequest request) {
        gameLog.clear();

        // 1. Load configuration (แก้ Path ให้หาเจอง่ายขึ้น)
        // ถ้าวางไฟล์ไว้หน้าสุดของโปรเจกต์ (ระดับเดียวกับ pom.xml) ให้ใช้แค่ "config.txt"
        ConfigLoader.loadConfig("config.txt");

        // 2. Determine Game Mode
        GameMode mode;
        try {
            mode = GameMode.valueOf(request.getGameMode().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new GameInitResponse(false, "Invalid game mode: " + request.getGameMode(), null);
        }

        // 3. ✅ สร้าง GameState และ Board *ก่อน* สร้าง Player
        // เพื่อให้ GameState.getBoard() ไม่เป็น null เมื่อ Player เรียกใช้
        board = HexGrid.getInstance(); // รีเซ็ตกระดาน
        gameState = GameState.getInstance(board); // ตั้งค่า GameState.board

        // 4. Setup Players based on mode
        List<Player> players = new ArrayList<>();
        Player player1, player2;

        switch (mode) {
            case DUEL:
                player1 = new Player("Player1");
                player2 = new Player("Player2");
                break;
            case SOLITAIRE:
                player1 = new Player("Player1");
                player2 = new Bot("Bot2");
                break;
            case AUTO:
                player1 = new Bot("Bot1");
                player2 = new Bot("Bot2");
                break;
            default:
                return new GameInitResponse(false, "Unsupported game mode", null);
        }

        // 5. Update Legacy Static Players
        GameState.players.clear();
        GameState.players.add(player1);
        GameState.players.add(player2);
        GameState.currentPlayer = player1;

        // 6. ✅ เรียก initilizeHex *หลังจาก* GameState มี board แล้ว
        player1.initilizeHex();
        player2.initilizeHex();

        // 7. Configure Minions
        GameSetup.minionTypes.clear();
        List<MinionType> minionTypes = configureMinionTypes(request.getMinionConfigs());
        GameSetup.minionTypes.addAll(minionTypes);

        // 8. Handle free spawn phase
        if (request.isWithFreeSpawn()) {
            handleFreeSpawnPhase();
        }

        addLog("Game initialized successfully in " + mode + " mode");

        return new GameInitResponse(true, "Game started", getGameStateDTO());
    }

    private void handleFreeSpawnPhase() {
        for (Player player : GameState.players) {
            if (player instanceof Bot) {
                ((Bot) player).spawnRandomMinion(true);
            }
        }
    }

    public CommandResponse executeCommand(CommandRequest request) {
        if (gameState == null) {
            return new CommandResponse(false, "Game not initialized", null);
        }

        Player currentPlayer = GameState.currentPlayer;

        try {
            CommandType type = request.getCommandTypeEnum();

            if (type == CommandType.BUY_HEX) {
                return handleBuyHex(currentPlayer, request.getRow(), request.getCol());
            } else if (type == CommandType.SPAWN_MINION) {
                return handleSpawnMinion(currentPlayer, request.getMinionTypeIndex(),
                        request.getRow(), request.getCol(), request.isFreeSpawn());
            } else {
                return new CommandResponse(false, "Unknown command type", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            addLog("Error executing command: " + e.getMessage());
            return new CommandResponse(false, e.getMessage(), getGameStateDTO());
        }
    }

    public TurnResponse endTurn() {
        if (gameState == null) {
            return new TurnResponse(false, "Game not initialized", null, false);
        }

        Player currentPlayer = GameState.currentPlayer;

        // 1. Execute Strategies
        executeMinionStrategies(currentPlayer);

        // 2. Check Game Over
        boolean gameOver = gameState.isGameOver();

        if (gameOver) {
            String winner = gameState.getWinnerName();
            addLog("Game Over! Winner: " + winner);
            return new TurnResponse(true, "Game ended", getGameStateDTO(), true);
        }

        // 3. Switch Player
        List<Player> players = GameState.players;
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();
        GameState.currentPlayer = players.get(nextIndex);

        // 4. Start Turn (Add Budget/Interest)
        GameState.currentPlayer.startTurn();

        // 5. Handle Bot Turn
        if (GameState.currentPlayer instanceof Bot) {
            handleBotTurn((Bot) GameState.currentPlayer);
        }

        addLog("Turn " + gameState.getTurnCounter() + " - " + GameState.currentPlayer.getName());
        return new TurnResponse(true, "Turn ended", getGameStateDTO(), false);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private CommandResponse handleBuyHex(Player player, int row, int col) {
        HexTile targetHex = board.getTile(row, col);
        if (targetHex == null) {
            return new CommandResponse(false, "Invalid hex coordinates", getGameStateDTO());
        }

        int prevHexCount = player.getOwnedHexes().size();
        player.purchaseHex(targetHex);
        int newHexCount = player.getOwnedHexes().size();

        if (newHexCount > prevHexCount) {
            addLog(player.getName() + " purchased hex at (" + (row + 1) + "," + (col + 1) + ")");
            return new CommandResponse(true, "Hex purchased successfully", getGameStateDTO());
        } else {
            return new CommandResponse(false, "Failed to purchase hex", getGameStateDTO());
        }
    }

    private CommandResponse handleSpawnMinion(Player player, int minionTypeIndex, int row, int col, boolean isFreeSpawn) {
        if (minionTypeIndex < 0 || minionTypeIndex >= GameSetup.minionTypes.size()) {
            return new CommandResponse(false, "Invalid minion type index", getGameStateDTO());
        }

        HexTile targetHex = board.getTile(row, col);
        if (targetHex == null) {
            return new CommandResponse(false, "Invalid coordinates", getGameStateDTO());
        }

        MinionType type = GameSetup.minionTypes.get(minionTypeIndex);
        Minion spawned = player.spawnMinion(targetHex, type, isFreeSpawn);

        if (spawned != null) {
            addLog(player.getName() + " spawned " + type.getCustomName() +
                    " at (" + (row + 1) + "," + (col + 1) + ")" + (isFreeSpawn ? " (free)" : ""));
            return new CommandResponse(true, "Minion spawned successfully", getGameStateDTO());
        } else {
            return new CommandResponse(false, "Failed to spawn (Budget/Cooldown/Occupied)", getGameStateDTO());
        }
    }

    private void executeMinionStrategies(Player player) {
        gameState.executeMinionStrategies(player);
    }

    private void handleBotTurn(Bot bot) {
        addLog(bot.getName() + " (Bot) is taking turn...");

        if (bot.getBudget() >= ConfigLoader.get("hex_purchase_cost")) {
            bot.purchaseRandomHex();
        }
        if (bot.canSpawn()) {
            bot.spawnRandomMinion(false);
        }
    }

    private List<MinionType> configureMinionTypes(List<MinionConfig> configs) {
        List<MinionType> types = new ArrayList<>();
        String[] defaultTypes = {"Cora", "Connie", "Charlotte", "Cody", "Crystal"};

        for (int i = 0; i < configs.size(); i++) {
            MinionConfig config = configs.get(i);
            String baseType = (i < defaultTypes.length) ? defaultTypes[i] : "Unknown";

            // Load strategy
            List<Statement> strategy = loadStrategyFromFile(config.getStrategyFile());
            if (strategy == null) {
                // Try parsing raw code if file not found
                strategy = parseStrategyCode(config.getStrategyCode() != null ? config.getStrategyCode() : "");
            }

            MinionType type = new MinionType(
                    baseType,
                    config.getCustomName(),
                    config.getDefenseFactor(),
                    strategy
            );
            types.add(type);
        }
        return types;
    }

    private List<Statement> parseStrategyCode(String code) {
        try {
            Tokenizer tokenizer = new Tokenizer(code);
            List<Token> tokens = tokenizer.tokenize();
            Parser parser = new Parser(tokens);
            return parser.parse();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<Statement> loadStrategyFromFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder strategyCode = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    strategyCode.append(line).append(" ");
                }
            }
            return parseStrategyCode(strategyCode.toString());
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== DTO CONVERSION ====================

    private GameStateDTO getGameStateDTO() {
        GameStateDTO dto = new GameStateDTO();
        dto.setTurnCounter(gameState.getTurnCounter());
        if (GameState.currentPlayer != null) {
            dto.setCurrentPlayerName(GameState.currentPlayer.getName());
        }
        dto.setGameLog(new ArrayList<>(gameLog));

        // Board
        List<HexTileDTO> hexTiles = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                HexTile tile = board.getTile(i, j);
                hexTiles.add(convertToHexTileDTO(tile));
            }
        }
        dto.setBoard(hexTiles);

        // Players
        List<PlayerDTO> playerDTOs = GameState.players.stream()
                .map(this::convertToPlayerDTO)
                .collect(Collectors.toList());
        dto.setPlayers(playerDTOs);

        return dto;
    }

    private HexTileDTO convertToHexTileDTO(HexTile tile) {
        HexTileDTO dto = new HexTileDTO();
        dto.setRow(tile.getRow());
        dto.setCol(tile.getCol());
        dto.setOccupied(tile.isOccupied());
        dto.setBought(tile.isBought());

        if (tile.getOwner() != null) {
            dto.setOwnerName(tile.getOwner().getShortName());
        }

        if (tile.isOccupied()) {
            Minion minion = tile.getMinion();
            MinionDTO minionDTO = new MinionDTO();
            minionDTO.setName(minion.getName());
            minionDTO.setHealth(minion.getHealth());
            minionDTO.setDefense(minion.getDefense());
            minionDTO.setOwnerName(minion.getOwner().getShortName());
            dto.setMinion(minionDTO);
        }
        return dto;
    }

    private PlayerDTO convertToPlayerDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setName(player.getName());
        dto.setShortName(player.getShortName());
        dto.setBudget((int) player.getBudget());
        dto.setOwnedHexCount(player.getOwnedHexes().size());
        dto.setMinionCount(player.getMinions().size());
        dto.setTotalSpawns(player.getTotalSpawns());
        dto.setCanSpawn(player.canSpawn());
        dto.setIsBot(player instanceof Bot);
        return dto;
    }

    private void addLog(String message) {
        gameLog.add(message);
        System.out.println("[GAME LOG] " + message);
    }
}
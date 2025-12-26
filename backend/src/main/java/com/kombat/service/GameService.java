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
    private boolean freeSpawnPhase = false;
    private int freeSpawnsCompleted = 0;

    public GameService() {
        this.gameLog = new ArrayList<>();
        this.board = HexGrid.getInstance();
    }

    public GameStateDTO getGameState() {
        if (gameState == null) {
            return null;
        }
        return getGameStateDTO();
    }

    public GameInitResponse initializeGame(GameInitRequest request) {
        // ✅ FULL RESET - Reset ทุกอย่างก่อนเริ่มเกมใหม่
        System.out.println("🔄 Resetting all game state...");

        // Reset GameState singleton
        GameState.reset();

        // Reset HexGrid singleton
        HexGrid.resetInstance();
        board = HexGrid.getInstance();

        // Reset GameSetup
        GameSetup.minionTypes.clear();

        // สร้าง gameState ใหม่
        gameState = GameState.getInstance(board);
        gameLog.clear();

        // Reset free spawn tracking
        freeSpawnPhase = request.isWithFreeSpawn();
        freeSpawnsCompleted = 0;

        System.out.println("✅ Reset complete!");

        // 1. Load configuration
        ConfigLoader.loadConfig("config.txt");

        // 2. Determine Game Mode
        GameMode mode;
        try {
            mode = GameMode.valueOf(request.getGameMode().toUpperCase());
            System.out.println("🎮 Game Mode: " + mode);
        } catch (IllegalArgumentException e) {
            return new GameInitResponse(false, "Invalid game mode: " + request.getGameMode(), null);
        }

        // 3. Setup Players based on mode
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

        // 4. Update GameState Players
        GameState.players.clear();
        GameState.players.add(player1);
        GameState.players.add(player2);
        GameState.currentPlayer = player1;

        // 5. ✅ Initialize starting hexes based on game mode
        initializeStartingHexes(player1, player2, mode);

        // 6. Configure Minions
        GameSetup.minionTypes.clear();
        List<MinionType> minionTypes = configureMinionTypes(request.getMinionConfigs());
        GameSetup.minionTypes.addAll(minionTypes);

        System.out.println("✅ Configured " + minionTypes.size() + " minion types");

        // 7. Handle free spawn phase
        if (request.isWithFreeSpawn()) {
            addLog("🎮 Free spawn phase started - Both players can spawn 1 free minion");
        }

        addLog("Game initialized successfully in " + mode + " mode");

        return new GameInitResponse(true, "Game started", getGameStateDTO());
    }

    // ✅ Helper method to initialize starting hexes
    private void initializeStartingHexes(Player player1, Player player2, GameMode mode) {
        System.out.println("🏠 Initializing starting hexes for " + mode + " mode");
        System.out.println("   Player 1: " + player1.getName() + " (type: " + player1.getClass().getSimpleName() + ")");
        System.out.println("   Player 2: " + player2.getName() + " (type: " + player2.getClass().getSimpleName() + ")");

        // Player 1 starting hexes (top-left corner)
        // Row 0: (0,0), (0,1), (0,2)
        // Row 1: (1,0), (1,1)
        HexTile hex00 = board.getTile(0, 0);
        HexTile hex01 = board.getTile(0, 1);
        HexTile hex02 = board.getTile(0, 2);
        HexTile hex10 = board.getTile(1, 0);
        HexTile hex11 = board.getTile(1, 1);

        if (hex00 != null) player1.purchaseHex(hex00);
        if (hex01 != null) player1.purchaseHex(hex01);
        if (hex02 != null) player1.purchaseHex(hex02);
        if (hex10 != null) player1.purchaseHex(hex10);
        if (hex11 != null) player1.purchaseHex(hex11);

        System.out.println("   ✅ Player 1 purchased " + player1.getOwnedHexes().size() + " hexes");

        // Player 2 starting hexes (bottom-right corner)
        // Row 7: (7,5), (7,6), (7,7)
        // Row 6: (6,6), (6,7)
        HexTile hex75 = board.getTile(7, 5);
        HexTile hex76 = board.getTile(7, 6);
        HexTile hex77 = board.getTile(7, 7);
        HexTile hex66 = board.getTile(6, 6);
        HexTile hex67 = board.getTile(6, 7);

        if (hex75 != null) player2.purchaseHex(hex75);
        if (hex76 != null) player2.purchaseHex(hex76);
        if (hex77 != null) player2.purchaseHex(hex77);
        if (hex66 != null) player2.purchaseHex(hex66);
        if (hex67 != null) player2.purchaseHex(hex67);

        System.out.println("   ✅ Player 2 purchased " + player2.getOwnedHexes().size() + " hexes");
        System.out.println("   💰 Player 1 budget after purchase: " + player1.getBudget());
        System.out.println("   💰 Player 2 budget after purchase: " + player2.getBudget());
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
                // ✅ Debug logging
                System.out.println("🎯 SPAWN_MINION command - Player: " + currentPlayer.getName());
                System.out.println("🎯 isFreeSpawn: " + request.isFreeSpawn());
                System.out.println("🎯 freeSpawnPhase: " + freeSpawnPhase);
                System.out.println("🎯 freeSpawnsCompleted: " + freeSpawnsCompleted);

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

        // 1. Execute Strategies (only if not in free spawn phase)
        if (!freeSpawnPhase) {
            executeMinionStrategies(currentPlayer);
        }

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

        // 4. ✅ Increment turn counter when returning to Player 1 (เพิ่มทีเดียวเท่านั้น!)
        if (nextIndex == 0 && !freeSpawnPhase) {
            GameState.turnCounter++;  // ✅ เพิ่มแค่ตัวเดียว
            System.out.println("🔄 Turn incremented to: " + GameState.turnCounter);
        }

        // 5. Start Turn (Add Budget/Interest) - only if not in free spawn phase
        if (!freeSpawnPhase) {
            GameState.currentPlayer.startTurn();
        }

        // 6. Handle Bot Turn
        if (GameState.currentPlayer instanceof Bot) {
            handleBotTurn((Bot) GameState.currentPlayer);
        }

        addLog("Turn " + GameState.turnCounter + " - " + GameState.currentPlayer.getName());
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
        System.out.println("🔍 handleSpawnMinion called - Player: " + player.getName() + ", isFreeSpawn: " + isFreeSpawn + ", freeSpawnPhase: " + freeSpawnPhase);

        if (minionTypeIndex < 0 || minionTypeIndex >= GameSetup.minionTypes.size()) {
            return new CommandResponse(false, "Invalid minion type index", getGameStateDTO());
        }

        HexTile targetHex = board.getTile(row, col);
        if (targetHex == null) {
            return new CommandResponse(false, "Invalid coordinates", getGameStateDTO());
        }

        MinionType type = GameSetup.minionTypes.get(minionTypeIndex);

        // ✅ For free spawn, bypass normal spawn restrictions
        Minion spawned;
        if (isFreeSpawn && freeSpawnPhase) {
            System.out.println("✅ Entering free spawn logic");
            spawned = player.spawnMinion(targetHex, type, true);

            if (spawned != null) {
                System.out.println("✅ Minion spawned successfully!");
                freeSpawnsCompleted++;
                addLog(player.getName() + " spawned " + type.getCustomName() +
                        " at (" + (row + 1) + "," + (col + 1) + ") (free spawn " + freeSpawnsCompleted + "/2)");

                // ✅ Switch to next player after free spawn
                List<Player> players = GameState.players;
                int currentIndex = players.indexOf(player);
                int nextIndex = (currentIndex + 1) % players.size();
                Player nextPlayer = players.get(nextIndex);
                GameState.currentPlayer = nextPlayer;

                System.out.println("🔄 Switched from " + player.getName() + " to " + nextPlayer.getName());
                addLog("🔄 Switched to " + nextPlayer.getName() + " for free spawn");

                // ✅ If both players have spawned, end free spawn phase
                if (freeSpawnsCompleted >= 2) {
                    freeSpawnPhase = false;
                    GameState.currentPlayer = players.get(0); // Reset to Player 1
                    GameState.currentPlayer.startTurn(); // Start first real turn
                    System.out.println("✅ Free spawn phase completed!");
                    addLog("✅ Free spawn phase completed! Starting turn 1");
                }

                return new CommandResponse(true, "Free minion spawned successfully", getGameStateDTO());
            } else {
                System.out.println("❌ Spawn failed - player.spawnMinion returned null");
                return new CommandResponse(false, "Failed to spawn minion on target hex", getGameStateDTO());
            }
        } else {
            System.out.println("⚠️ Not in free spawn mode - isFreeSpawn: " + isFreeSpawn + ", freeSpawnPhase: " + freeSpawnPhase);
            spawned = player.spawnMinion(targetHex, type, false);

            if (spawned != null) {
                addLog(player.getName() + " spawned " + type.getCustomName() +
                        " at (" + (row + 1) + "," + (col + 1) + ")");
                return new CommandResponse(true, "Minion spawned successfully", getGameStateDTO());
            }
        }

        return new CommandResponse(false, "Failed to spawn (Budget/Cooldown/Occupied/Not your hex)", getGameStateDTO());
    }

    private void executeMinionStrategies(Player player) {
        gameState.executeMinionStrategies(player);
    }

    private void handleBotTurn(Bot bot) {
        addLog(bot.getName() + " (Bot) is taking turn...");

        if (freeSpawnPhase) {
            bot.spawnRandomMinion(true);
            freeSpawnsCompleted++;

            if (freeSpawnsCompleted >= 2) {
                freeSpawnPhase = false;
                GameState.currentPlayer = GameState.players.get(0);
                GameState.currentPlayer.startTurn();
                addLog("✅ Free spawn phase completed! Starting turn 1");
            }
            return;
        }

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
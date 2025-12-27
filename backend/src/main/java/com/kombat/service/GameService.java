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
    private boolean isBotPlaying = false;

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
        // FULL RESET - Reset everything before starting new game
        System.out.println("🔄 Resetting all game state...");

        GameState.reset();
        HexGrid.resetInstance();
        board = HexGrid.getInstance();
        GameSetup.minionTypes.clear();
        gameLog.clear();

        // Reset free spawn tracking
        freeSpawnPhase = request.isWithFreeSpawn();
        freeSpawnsCompleted = 0;
        isBotPlaying = false;

        System.out.println("✅ Reset complete!");

        // 1. Load configuration
        ConfigLoader.loadConfig("config.txt");

        // Verify config was loaded successfully
        if (!ConfigLoader.isConfigLoaded()) {
            System.err.println("❌❌ CRITICAL: Failed to load config!");
            return new GameInitResponse(false, "Failed to load config file", null);
        }

        // Print all loaded settings
        System.out.println("📋 Loaded config settings:");
        for (Map.Entry<String, Integer> entry : ConfigLoader.getAllSettings().entrySet()) {
            System.out.println("   " + entry.getKey() + " = " + entry.getValue());
        }

        // CREATE GameState (after config is loaded)
        gameState = GameState.getInstance(board);

        // Verify maxTurns was set correctly
        System.out.println("✅ GameState.maxTurns = " + GameState.maxTurns);
        if (GameState.maxTurns <= 1) {
            System.err.println("⚠️ WARNING: maxTurns is too low! Setting to 69");
            GameState.maxTurns = 69;
        }

        System.out.println("✅ Initialization complete!");

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

        // CRITICAL: Set Player 1 as starting player
        GameState.currentPlayer = player1;

        // CRITICAL: Initialize turn counter based on free spawn mode
        if (request.isWithFreeSpawn()) {
            GameState.turnCounter = 0; // Will be set to 1 after free spawn completes
            System.out.println("📢 Turn counter: 0 (free spawn phase)");
        } else {
            GameState.turnCounter = 1; // Start at turn 1 immediately
            System.out.println("📢 Turn counter: 1 (no free spawn)");
        }

        System.out.println("👤 Starting player: " + player1.getName());

        // 5. Initialize starting hexes based on game mode
        initializeStartingHexes(player1, player2, mode);

        // 6. Configure Minions
        GameSetup.minionTypes.clear();
        List<MinionType> minionTypes = configureMinionTypes(request.getMinionConfigs());
        GameSetup.minionTypes.addAll(minionTypes);

        System.out.println("✅ Configured " + minionTypes.size() + " minion types");

        // 7. Handle free spawn phase
        if (request.isWithFreeSpawn()) {
            addLog("🎮 Free spawn phase started - Both players can spawn 1 free minion");
            System.out.println("🎮 Free spawn: Current player is " + GameState.currentPlayer.getName());

        } else {
            // If no free spawn, start Player 1's turn immediately
            GameState.currentPlayer.startTurn();
            addLog("Turn 1 - " + GameState.currentPlayer.getName());
        }

        addLog("Game initialized successfully in " + mode + " mode");

        return new GameInitResponse(true, "Game started", getGameStateDTO());
    }

    // Helper method to initialize starting hexes
    private void initializeStartingHexes(Player player1, Player player2, GameMode mode) {
        System.out.println("🏠 Initializing starting hexes for " + mode + " mode");
        System.out.println("   Player 1: " + player1.getName() + " (type: " + player1.getClass().getSimpleName() + ")");
        System.out.println("   Player 2: " + player2.getName() + " (type: " + player2.getClass().getSimpleName() + ")");

        // Check budget BEFORE assigning starting hexes
        System.out.println("   💰 Player 1 budget BEFORE: " + player1.getBudget());
        System.out.println("   💰 Player 2 budget BEFORE: " + player2.getBudget());

        // Player 1 starting hexes (top-left corner) - FREE
        HexTile hex00 = board.getTile(0, 0);
        HexTile hex01 = board.getTile(0, 1);
        HexTile hex02 = board.getTile(0, 2);
        HexTile hex10 = board.getTile(1, 0);
        HexTile hex11 = board.getTile(1, 1);

        int p1Count = 0;
        // Use direct assignment instead of purchaseHex() to avoid cost
        if (hex00 != null) { assignHexToPlayer(player1, hex00); p1Count++; }
        if (hex01 != null) { assignHexToPlayer(player1, hex01); p1Count++; }
        if (hex02 != null) { assignHexToPlayer(player1, hex02); p1Count++; }
        if (hex10 != null) { assignHexToPlayer(player1, hex10); p1Count++; }
        if (hex11 != null) { assignHexToPlayer(player1, hex11); p1Count++; }

        System.out.println("   ✅ Player 1 received " + p1Count + " starting hexes (FREE)");

        // Player 2 starting hexes (bottom-right corner) - FREE
        HexTile hex75 = board.getTile(7, 5);
        HexTile hex76 = board.getTile(7, 6);
        HexTile hex77 = board.getTile(7, 7);
        HexTile hex66 = board.getTile(6, 6);
        HexTile hex67 = board.getTile(6, 7);

        int p2Count = 0;
        // Use direct assignment instead of purchaseHex() to avoid cost
        if (hex75 != null) { assignHexToPlayer(player2, hex75); p2Count++; }
        if (hex76 != null) { assignHexToPlayer(player2, hex76); p2Count++; }
        if (hex77 != null) { assignHexToPlayer(player2, hex77); p2Count++; }
        if (hex66 != null) { assignHexToPlayer(player2, hex66); p2Count++; }
        if (hex67 != null) { assignHexToPlayer(player2, hex67); p2Count++; }

        System.out.println("   ✅ Player 2 received " + p2Count + " starting hexes (FREE)");

        // Check budget AFTER - should be unchanged!
        System.out.println("   💰 Player 1 budget AFTER: " + player1.getBudget());
        System.out.println("   💰 Player 2 budget AFTER: " + player2.getBudget());

        // CRITICAL: Verify hexes are actually owned
        System.out.println("   📋 Player 1 owned hexes count: " + player1.getOwnedHexes().size());
        System.out.println("   📋 Player 2 owned hexes count: " + player2.getOwnedHexes().size());
    }

    private void assignHexToPlayer(Player player, HexTile hex) {
        if (hex != null && !hex.isBought()) {
            player.getOwnedHexes().add(hex);
            hex.owner = player;
            // NO budget deduction for starting hexes!
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
                // Debug logging
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

        // CRITICAL: During free spawn phase, ONLY handle bot spawning
        if (freeSpawnPhase) {
            System.out.println("🎯 endTurn() called during FREE SPAWN phase");

            // If current player is a bot, handle their free spawn
            if (currentPlayer instanceof Bot) {
                handleBotTurn((Bot) currentPlayer);
            }

            // Don't do anything else during free spawn
            return new TurnResponse(true, "Free spawn turn", getGameStateDTO(), false);
        }

        // Normal turn logic (after free spawn)

        // 1. Execute Strategies
        executeMinionStrategies(currentPlayer);

        // 2. Check Game Over IMMEDIATELY after strategies
        boolean gameOver = gameState.isGameOver();
        if (gameOver) {
            String winner = gameState.getWinnerName();
            addLog("Game Over! Winner: " + winner);
            System.out.println("🏁 GAME OVER - Stopping all execution");
            return new TurnResponse(true, "Game ended", getGameStateDTO(), true);
        }

        // 3. Switch Player
        List<Player> players = GameState.players;
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();
        GameState.currentPlayer = players.get(nextIndex);

        // 4. Increment turn counter when returning to Player 1
        if (nextIndex == 0) {
            GameState.turnCounter++;
            System.out.println("🔄 Turn incremented to: " + GameState.turnCounter);

            // Check Game Over AGAIN after turn increment
            if (gameState.isGameOver()) {
                String winner = gameState.getWinnerName();
                addLog("Game Over! Winner: " + winner);
                System.out.println("🏁 GAME OVER after turn increment - Max turns reached");
                return new TurnResponse(true, "Game ended", getGameStateDTO(), true);
            }

            addLog("Turn " + GameState.turnCounter + " - " + GameState.currentPlayer.getName());
        } else {
            addLog("Turn " + GameState.turnCounter + " - " + GameState.currentPlayer.getName());
        }

        // 5. Start Turn
        GameState.currentPlayer.startTurn();

        // 6. Handle Bot Turn (but stop if game over after bot plays)
        if (GameState.currentPlayer instanceof Bot) {
            handleBotTurn((Bot) GameState.currentPlayer);

            // Check Game Over after bot turn
            if (gameState.isGameOver()) {
                String winner = gameState.getWinnerName();
                addLog("Game Over! Winner: " + winner);
                System.out.println("🏁 GAME OVER after bot turn");
                return new TurnResponse(true, "Game ended", getGameStateDTO(), true);
            }
        }

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
        System.out.println("🔍 handleSpawnMinion called - Player: " + player.getName() +
                ", isFreeSpawn: " + isFreeSpawn +
                ", freeSpawnPhase: " + freeSpawnPhase);

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

                // Switch to next player after free spawn
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
                    GameState.turnCounter = 1; // ✅ Set turn counter to 1
                    GameState.currentPlayer.startTurn(); // Start first real turn

                    System.out.println("✅ Free spawn phase completed!");
                    addLog("✅ Free spawn phase completed! Starting Turn 1");
                    addLog("Turn 1 - " + GameState.currentPlayer.getName());
                }

                return new CommandResponse(true, "Free minion spawned successfully", getGameStateDTO());
            } else {
                System.out.println("❌ Spawn failed - player.spawnMinion returned null");
                return new CommandResponse(false, "Failed to spawn minion on target hex", getGameStateDTO());
            }
        } else {
            // ✅ Normal spawn (not free spawn)
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
        System.out.println("🤖 Bot " + bot.getName() + " is taking turn...");

        // ✅ Prevent duplicate bot calls
        if (isBotPlaying) {
            System.out.println("⚠️ Bot already playing, skipping");
            return;
        }
        isBotPlaying = true;

        try {
            // ✅ Handle free spawn phase separately
            if (freeSpawnPhase) {
                System.out.println("🤖 Bot free spawn - " + bot.getName());
                bot.spawnRandomMinion(true);
                freeSpawnsCompleted++;

                addLog(bot.getName() + " spawned free minion (" + freeSpawnsCompleted + "/2)");

                // ✅ Check if free spawn is complete BEFORE switching
                if (freeSpawnsCompleted >= 2) {
                    freeSpawnPhase = false;
                    List<Player> players = GameState.players;
                    GameState.currentPlayer = players.get(0); // Start with Player 1
                    GameState.turnCounter = 1; // ✅ Set turn counter to 1
                    GameState.currentPlayer.startTurn(); // Start first real turn

                    addLog("✅ Free spawn phase completed! Starting Turn 1");
                    addLog("Turn 1 - " + GameState.currentPlayer.getName());
                    System.out.println("✅ Free spawn completed. Turn counter: " + GameState.turnCounter);
                    System.out.println("✅ Next player for turn 1: " + GameState.currentPlayer.getName());
                } else {
                    // Switch to next player for their free spawn
                    List<Player> players = GameState.players;
                    int currentIndex = players.indexOf(bot);
                    int nextIndex = (currentIndex + 1) % players.size();
                    Player nextPlayer = players.get(nextIndex);
                    GameState.currentPlayer = nextPlayer;

                    System.out.println("🔄 Switched from " + bot.getName() + " to " + nextPlayer.getName() + " for free spawn");
                }

                return; // ✅ Exit immediately after free spawn
            }

            // ✅ Normal turn logic (after free spawn phase)
            addLog(bot.getName() + " (Bot) is taking turn...");
            System.out.println("🤖 Bot normal turn - Budget: " + bot.getBudget());

            // Try to purchase hex
            if (bot.getBudget() >= ConfigLoader.get("hex_purchase_cost")) {
                System.out.println("🤖 Bot attempting to purchase hex");
                bot.purchaseRandomHex();
            }

            // Try to spawn minion
            if (bot.canSpawn()) {
                System.out.println("🤖 Bot attempting to spawn minion");
                bot.spawnRandomMinion(false);
            }

            System.out.println("🤖 Bot turn completed");

        } catch (Exception e) {
            System.err.println("❌ Bot turn error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            isBotPlaying = false;
        }
    }

    private List<MinionType> configureMinionTypes(List<MinionConfig> configs) {
        List<MinionType> types = new ArrayList<>();
        String[] defaultTypes = {"Cora", "Connie", "Charlotte", "Cody", "Crystal"};

        for (int i = 0; i < configs.size(); i++) {
            MinionConfig config = configs.get(i);
            String baseType = (i < defaultTypes.length) ? defaultTypes[i] : "Unknown";

            // ✅ เพิ่ม debug log
            System.out.println("🎯 Configuring minion " + i + ":");
            System.out.println("   customName: " + config.getCustomName());
            System.out.println("   defenseFactor: " + config.getDefenseFactor());
            System.out.println("   strategyCode length: " +
                    (config.getStrategyCode() != null ? config.getStrategyCode().length() : 0));
            System.out.println("   strategyCode: " +
                    (config.getStrategyCode() != null ? config.getStrategyCode().substring(0, Math.min(100, config.getStrategyCode().length())) : "null"));

            // Load strategy
            List<Statement> strategy = loadStrategyFromFile(config.getStrategyFile());
            if (strategy == null) {
                strategy = parseStrategyCode(config.getStrategyCode() != null ? config.getStrategyCode() : "");
            }

            System.out.println("   ✅ Parsed strategy statements: " + (strategy != null ? strategy.size() : 0));

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
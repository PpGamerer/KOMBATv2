package com.kombat.controller;

import com.kombat.dto.*;
import com.kombat.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*") // อนุญาตให้ Frontend (Port 3000) เรียกเข้ามาได้
public class EnhancedGameController {

    private final GameService gameService;

    // Inject GameService เข้ามาทำงานแทน
    @Autowired
    public EnhancedGameController(GameService gameService) {
        this.gameService = gameService;
    }

    // 1. Init Game
    @PostMapping("/init")
    public ResponseEntity<?> initGame(@RequestBody GameInitRequest request) {
        // DTO 'GameInitRequest' ที่เราสร้างในโฟลเดอร์ dto/ มีตัวแปรชื่อ 'minionConfigs'
        // ซึ่งตรงกับ Frontend แล้ว ดังนั้นจะไม่ null ครับ
        try {
            GameInitResponse response = gameService.initializeGame(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // 2. Get State
    @GetMapping("/state")
    public ResponseEntity<?> getGameState() {
        try {
            GameStateDTO state = gameService.getGameState();
            if (state == null) {
                return ResponseEntity.badRequest().body("Game not started");
            }
            // Wrap ใส่ Response เพื่อความสม่ำเสมอ
            return ResponseEntity.ok(new GameInitResponse(true, "Current State", state));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // 3. Spawn Minion
    @PostMapping("/spawn")
    public ResponseEntity<?> spawnMinion(@RequestBody CommandRequest request) {
        // บังคับ Set Type เป็น SPAWN_MINION
        request.setCommandType("SPAWN_MINION");
        CommandResponse response = gameService.executeCommand(request);
        return ResponseEntity.ok(response);
    }

    // 4. Purchase Hex
    @PostMapping("/purchase-hex")
    public ResponseEntity<?> purchaseHex(@RequestBody CommandRequest request) {
        // บังคับ Set Type เป็น BUY_HEX
        request.setCommandType("BUY_HEX");
        CommandResponse response = gameService.executeCommand(request);
        return ResponseEntity.ok(response);
    }

    // 5. End Turn
    @PostMapping("/end-turn")
    public ResponseEntity<?> endTurn() {
        TurnResponse response = gameService.endTurn();
        return ResponseEntity.ok(response);
    }

    // 6. Select Mode (Legacy Support)
    @PostMapping("/select-mode")
    public ResponseEntity<?> selectMode() {
        return ResponseEntity.ok().build();
    }
}
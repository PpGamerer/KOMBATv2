package kombat.websocket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class SelectedMinionsController {

    // ✅ รับ POST จาก frontend
    @PostMapping("/selected-minions")
    public ResponseEntity<String> receiveSelectedMinions(@RequestBody Map<String, List<Map<String, Object>>> body) {
        List<Map<String, Object>> minions = body.get("minions");

        System.out.println("📨 [BACKEND] Received selected minions:");
        for (Map<String, Object> minion : minions) {
            System.out.println("🧸 " + minion);
        }

        return ResponseEntity.ok("Minions received successfully");
    }

    @GetMapping("/selected-minions")
    public ResponseEntity<Map<String, String>> handleInvalidGet() {
        Map<String, String> error = new HashMap<>();
        error.put("error", "This endpoint only supports POST.");
        return ResponseEntity.badRequest().body(error);
    }
}
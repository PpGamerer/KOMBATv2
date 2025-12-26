package kombat.websocket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MinionSettingsController {

    public MinionSettingsController() {
        System.out.println("🚀 [BACKEND] ✅ MinionSettingsController loaded!");
    }

    @PostMapping("/minion-settings")
    public ResponseEntity<String> receiveMinionSettings(@RequestBody Map<String, List<Map<String, Object>>> body) {
        List<Map<String, Object>> minions = body.get("minions");

        System.out.println("🎯 [BACKEND] Minion Settings Received:");
        for (Map<String, Object> m : minions) {
            System.out.println("🛡️ " + m);
        }

        return ResponseEntity.ok("Minion settings received successfully.");
    }
}
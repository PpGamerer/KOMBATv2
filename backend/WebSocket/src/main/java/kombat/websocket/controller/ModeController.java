package kombat.websocket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ModeController {

    @PostMapping(value = "/select-mode", produces = "application/json")
    public ResponseEntity<Map<String, String>> selectMode(@RequestBody Map<String, Integer> body) {
        Integer mode = body.get("mode");
        System.out.println("📨 [BACKEND] Selected mode: " + mode);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Mode received: " + mode);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/select-mode")
    public ResponseEntity<Map<String, String>> handleInvalidGet() {
        Map<String, String> error = new HashMap<>();
        error.put("error", "This endpoint only supports POST.");
        return ResponseEntity.badRequest().body(error);
    }
}
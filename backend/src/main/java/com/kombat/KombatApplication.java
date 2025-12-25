package com.kombat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.kombat",
        "Controller",
        "Game",
        "Board",
        "Player",
        "Minion",
        "Parser",
        "Tokenizer",
        "Interfaces"
})
public class KombatApplication {
    public static void main(String[] args) {
        SpringApplication.run(KombatApplication.class, args);
        System.out.println("🎮 KOMBAT Backend Started!");
        System.out.println("📡 API: http://localhost:8080/api");
    }
}
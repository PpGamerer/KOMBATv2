package Game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    private static Map<String, Integer> settings = new HashMap<>();

    // โหลดค่า Config จากไฟล์
    public static void loadConfig(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("=")) {
                    String[] parts = line.split("=");
                    String key = parts[0].trim();
                    int value = Integer.parseInt(parts[1].trim());
                    settings.put(key, value);
                }
            }
            System.out.println("Configuration loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading config file: " + e.getMessage());
        }
    }
    // ดึงค่าจาก Config
    public static int get(String key) {
        return settings.getOrDefault(key, 0);
    }
}

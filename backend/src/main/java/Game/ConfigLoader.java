package Game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    private static Map<String, Integer> settings = new HashMap<>();
    private static boolean configLoaded = false;

    // โหลดค่า Config จากไฟล์
    public static void loadConfig(String filePath) {
        // Try multiple paths
        String[] paths = {
                filePath,
                "config.txt",
                "backend/config.txt",
                "../config.txt",
                "src/main/resources/config.txt"
        };

        for (String path : paths) {
            File file = new File(path);
            System.out.println("🔍 Trying to load config from: " + path);
            System.out.println("   Absolute path: " + file.getAbsolutePath());
            System.out.println("   File exists: " + file.exists());

            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    settings.clear(); // Clear previous settings
                    String line;
                    int lineCount = 0;

                    while ((line = br.readLine()) != null) {
                        if (line.contains("=")) {
                            String[] parts = line.split("=");
                            String key = parts[0].trim();
                            int value = Integer.parseInt(parts[1].trim());
                            settings.put(key, value);
                            lineCount++;
                            System.out.println("   ✅ " + key + " = " + value);
                        }
                    }

                    configLoaded = true;
                    System.out.println("✅✅ Configuration loaded successfully from: " + path);
                    System.out.println("   Total settings loaded: " + lineCount);
                    return; // Success - exit

                } catch (IOException e) {
                    System.err.println("❌ Error reading config file: " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.err.println("❌ Error parsing config value: " + e.getMessage());
                }
            }
        }

        // If we get here, no config was loaded successfully
        System.err.println("❌❌ CRITICAL: Could not load config from any path!");
        System.err.println("   Working directory: " + System.getProperty("user.dir"));
        configLoaded = false;
    }

    // ดึงค่าจาก Config
    public static int get(String key) {
        if (!configLoaded) {
            System.err.println("⚠️ WARNING: Config not loaded! Returning 0 for key: " + key);
        }
        return settings.getOrDefault(key, 0);
    }

    // Check if config was loaded successfully
    public static boolean isConfigLoaded() {
        return configLoaded;
    }

    // Get all settings (for debugging)
    public static Map<String, Integer> getAllSettings() {
        return new HashMap<>(settings);
    }
}
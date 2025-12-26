package Game;

import Minion.MinionType;
import Parser.AST.Statement;
import Parser.Parser;
import Player.Player;
import Tokenizer.Token;
import Tokenizer.Tokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameSetup {
    private final Scanner scanner = new Scanner(System.in);
    public static final List<MinionType> minionTypes = new ArrayList<>();
    private final List<Player> players = GameState.players;

    public static void configureMinionTypes() {
        Scanner scanner = new Scanner(System.in);
        String[] defaultTypes = {"Cora", "Connie", "Charlotte", "Cody", "Crystal"};

        System.out.print("Choose the number of minion types (1-5):");
        int numTypes = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        for (int i = 0; i < numTypes; i++) {
            System.out.println("Minion Type " + (i + 1) + " (Default: " + defaultTypes[i] + ")");
            System.out.print("Enter custom name (or press Enter for default): ");
            String customName = scanner.nextLine();

            System.out.print("Enter defense factor: ");
            int defenseFactor = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter strategy file: ");
            String strategyFile = scanner.nextLine();

            List<Statement> strategy = loadStrategyFromFile(strategyFile);

            MinionType type = new MinionType(defaultTypes[i],customName,defenseFactor,strategy);

            System.out.println("Strategy assigned to " + type.getCustomName());
            minionTypes.add(type);
        }
    }

    private static List<Statement> loadStrategyFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder strategyCode = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    strategyCode.append(line).append(" ");
                }
            }

            Tokenizer tokenizer = new Tokenizer(strategyCode.toString());
            List<Token> tokens = tokenizer.tokenize();
            Parser parser = new Parser(tokens);
            List<Statement> strategy = parser.parse();
            return strategy;
        } catch (Exception e) {
            System.out.println("Error loading strategy: " + e.getMessage());
        }
        return null;
    }
}

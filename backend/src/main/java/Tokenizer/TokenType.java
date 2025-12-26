package Tokenizer;

import java.util.*;

/**
 * Enum representing different token types in the tokenizer.
 */
public enum TokenType {
    IDENTIFIER, NUMBER, OPERATOR, LPAREN, RPAREN, LBRACE, RBRACE,
    EQUALS, PLUS, MINUS, MULT, DIV, MOD, POWER,
    IF, THEN, ELSE, WHILE, MOVE, SHOOT, DONE,
    UP, DOWN, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT,
    ALLY, OPPONENT, NEARBY, EOF;

    private static final Set<String> KEYWORDS = Set.of(
            "if", "then", "else", "while", "move", "shoot", "done",
            "up", "down", "upleft", "upright", "downleft", "downright",
            "ally", "opponent", "nearby"
    );

    /**
     * Converts a word to its corresponding TokenType.
     * @param word the word to convert
     * @return the corresponding TokenType
     */
    public static TokenType fromWord(String word) {
        return KEYWORDS.contains(word) ? TokenType.valueOf(word.toUpperCase()) : IDENTIFIER;
    }

    /**
     * Converts a character to its corresponding TokenType.
     * @param c the character to convert
     * @return the corresponding TokenType
     */
    public static TokenType fromChar(char c) {
        return switch (c) {
            case '=' -> EQUALS;
            case '+' -> PLUS;
            case '-' -> MINUS;
            case '*' -> MULT;
            case '/' -> DIV;
            case '%' -> MOD;
            case '^' -> POWER;
            case '(' -> LPAREN;
            case ')' -> RPAREN;
            case '{' -> LBRACE;
            case '}' -> RBRACE;
            default -> OPERATOR;
        };
    }
}

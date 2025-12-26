package Tokenizer;

import java.util.ArrayList;

import java.util.List;
public class Tokenizer {
    private final String input;
    private int currentPosition = 0;

    public Tokenizer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = nextToken()).getType() != TokenType.EOF) {
            tokens.add(token);
        }
        tokens.add(new Token(TokenType.EOF, "EOF")); // Add EOF token at the end
        return tokens;
    }

    // Returns the next token in the input string
    public Token nextToken() {
        if (currentPosition >= input.length()) {
            return new Token(TokenType.EOF, "EOF");
        }

        char currentChar = input.charAt(currentPosition);

        // Skip whitespaces
        if (Character.isWhitespace(currentChar)) {
            currentPosition++;
            return nextToken();
        }

        // If it's a letter, consider it as an identifier or keyword
        if (Character.isLetter(currentChar)) {
            StringBuilder word = new StringBuilder();
            while (currentPosition < input.length() && Character.isLetterOrDigit(input.charAt(currentPosition))) {
                word.append(input.charAt(currentPosition));
                currentPosition++;
            }
            TokenType type = TokenType.fromWord(word.toString());
            return new Token(type, word.toString());
        }

        // If it's a digit, consider it as a number
        if (Character.isDigit(currentChar)) {
            StringBuilder number = new StringBuilder();
            while (currentPosition < input.length() && Character.isDigit(input.charAt(currentPosition))) {
                number.append(input.charAt(currentPosition));
                currentPosition++;
            }
            return new Token(TokenType.NUMBER, number.toString());
        }

        // If it's a special character, convert it to an operator token
        TokenType type = TokenType.fromChar(currentChar);
        String tokenValue = String.valueOf(currentChar);
        currentPosition++;
        return new Token(type, tokenValue);
    }

    // Peek at the current token without advancing
    public Token peek() {
        if (currentPosition >= input.length()) {
            return new Token(TokenType.EOF, "EOF");
        }

        // Save the current position to avoid advancing it
        int savedPosition = currentPosition;

        char currentChar = input.charAt(currentPosition);

        // Skip whitespaces
        if (Character.isWhitespace(currentChar)) {
            currentPosition++;
            return peek();
        }

        // If it's a letter, consider it as an identifier or keyword
        if (Character.isLetter(currentChar)) {
            StringBuilder word = new StringBuilder();
            while (currentPosition < input.length() && Character.isLetterOrDigit(input.charAt(currentPosition))) {
                word.append(input.charAt(currentPosition));
                currentPosition++;
            }
            TokenType type = TokenType.fromWord(word.toString());
            currentPosition = savedPosition; // Restore the position after peeking
            return new Token(type, word.toString());
        }

        // If it's a digit, consider it as a number
        if (Character.isDigit(currentChar)) {
            StringBuilder number = new StringBuilder();
            while (currentPosition < input.length() && Character.isDigit(input.charAt(currentPosition))) {
                number.append(input.charAt(currentPosition));
                currentPosition++;
            }
            currentPosition = savedPosition; // Restore the position after peeking
            return new Token(TokenType.NUMBER, number.toString());
        }

        // If it's a special character, convert it to an operator token
        TokenType type = TokenType.fromChar(currentChar);
        String tokenValue = String.valueOf(currentChar);
        currentPosition = savedPosition; // Restore the position after peeking
        return new Token(type, tokenValue);
    }

    // Check if there are more tokens
    public boolean hasNextToken() {
        return currentPosition < input.length();
    }

    // Consume the next token of a specific type
    public void consume(TokenType tokenType) {
        Token currentToken = nextToken();
        if (currentToken.getType() != tokenType) {
            throw new RuntimeException("Expected token of type " + tokenType + " but got " + currentToken.getType());
        }
    }

    // Get the current position in the input string
    public int getPosition() {
        return currentPosition;
    }
}
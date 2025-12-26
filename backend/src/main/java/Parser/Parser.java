package Parser;

import Parser.AST.*;
import Tokenizer.Token;
import Tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int currentPosition;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentPosition = 0;
    }

    private Token getCurrentToken() {
        return tokens.get(currentPosition);
    }

    private void consume() {
        if (currentPosition < tokens.size()) {
            currentPosition++;
        }
    }

    private boolean expect(TokenType type) {
        return getCurrentToken().getType() == type;
    }

    public List<Statement> parse() {
        List<Statement> strategy = new ArrayList<>();
        while (currentPosition < tokens.size() && !expect(TokenType.EOF)) {
            strategy.add(parseStatement());
        }
        return strategy;
    }

    private Statement parseStatement() {
        Token token = getCurrentToken();
        if (token.getType() == TokenType.IDENTIFIER) {
            return parseAssignment();
        } else if (token.getType() == TokenType.IF) {
            return parseIfStatement();
        } else if (token.getType() == TokenType.WHILE) {
            return parseWhileStatement();
        } else if (token.getType() == TokenType.LBRACE) {
            return parseBlockStatement();
        } else if (isActionCommand(token)) {
            return parseActionCommand();
        }
        throw new RuntimeException("Unexpected token: " + token.getValue());
    }

    private Statement parseAssignment() {
        String identifier = getCurrentToken().getValue();
        consume(); // consume identifier
        if (!expect(TokenType.EQUALS)) {
            throw new RuntimeException("Expected '=' after identifier");
        }
        consume(); // consume '='
        Expression expr = parseExpression();
        return new AssignmentStatement(identifier, expr);
    }

    private Statement parseIfStatement() {
        consume(); // consume 'if'
        if (!expect(TokenType.LPAREN)) throw new RuntimeException("Expected '(' after 'if'");
        consume(); // consume '('
        Expression condition = parseExpression();
        if (!expect(TokenType.RPAREN)) throw new RuntimeException("Expected ')' after condition");
        consume(); // consume ')'
        if (!expect(TokenType.THEN)) throw new RuntimeException("Expected 'then' after condition");
        consume(); // consume 'then'
        Statement thenBranch = parseStatement();
        if (!expect(TokenType.ELSE)) throw new RuntimeException("Expected 'else'");
        consume(); // consume 'else'
        Statement elseBranch = parseStatement();
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement parseWhileStatement() {
        consume(); // consume 'while'
        if (!expect(TokenType.LPAREN)) throw new RuntimeException("Expected '(' after 'while'");
        consume(); // consume '('
        Expression condition = parseExpression();
        if (!expect(TokenType.RPAREN)) throw new RuntimeException("Expected ')' after condition");
        consume(); // consume ')'
        Statement body = parseStatement();
        return new WhileStatement(condition, body);
    }

    private Statement parseActionCommand() {
        Token token = getCurrentToken();
        if (token.getType() == TokenType.DONE) {
            consume();
            return new DoneCommand();
        } else if (token.getType() == TokenType.MOVE) {
            consume(); // consume 'move'
            if (!isDirection(getCurrentToken())) throw new RuntimeException("Expected a direction after 'move'");
            String direction = getCurrentToken().getValue();
            consume();
            return new MoveCommand(direction);
        } else if (token.getType() == TokenType.SHOOT) {
            consume(); // consume 'shoot'
            if (!isDirection(getCurrentToken())) throw new RuntimeException("Expected a direction after 'shoot'");
            String direction = getCurrentToken().getValue();
            consume();
            Expression power = parseExpression();  // Power is an expression
            return new AttackCommand(direction, power);
        }

        throw new RuntimeException("Unknown action command: " + token.getValue());
    }

    private Statement parseBlockStatement() {
        consume(); // consume '{'
        List<Statement> statements = new ArrayList<>();
        while (!expect(TokenType.RBRACE)) {
            statements.add(parseStatement());
        }
        consume(); // consume '}'
        return new BlockStatement(statements);
    }

    private Expression parseExpression() {
        Token token = getCurrentToken();
        if (token.getType() == TokenType.OPERATOR && (token.getValue().equals("<") || token.getValue().equals(">")
                || token.getValue().equals("==") || token.getValue().equals("<=") || token.getValue().equals(">="))) {
            throw new RuntimeException("Comparison operators are not allowed in this grammar: " + token.getValue());
        }

        Expression expr = parseTerm();
        while (expect(TokenType.PLUS) || expect(TokenType.MINUS)) {
            Token op = getCurrentToken();
            consume();
            Expression right = parseTerm();
            expr = new BinaryExpression(expr, op.getValue(), right);
        }
        return expr;
    }


    private Expression parseTerm() {
        Expression term = parseFactor();
        while (expect(TokenType.MULT) || expect(TokenType.DIV) || expect(TokenType.MOD)) {
            Token op = getCurrentToken();
            consume();
            Expression right = parseFactor();
            term = new BinaryExpression(term, op.getValue(), right);
        }
        return term;
    }

    private Expression parseFactor() {
        Expression factor = parsePrimary();
        if (expect(TokenType.POWER)) {
            consume();
            Expression right = parseFactor();
            factor = new BinaryExpression(factor, "^", right);
        }
        return factor;
    }

    private Expression parsePrimary() {
        Token token = getCurrentToken();

        if (token.getType() == TokenType.NUMBER) {
            consume();
            return new NumberExpression(Long.parseLong(token.getValue()));
        } else if (token.getType() == TokenType.IDENTIFIER) {
            consume();
            return new IdentifierExpression(token.getValue());
        } else if (expect(TokenType.LPAREN)) {
            consume();
            Expression expr = parseExpression();
            if (!expect(TokenType.RPAREN)) throw new RuntimeException("Expected ')' after expression");
            consume();
            return expr;
        } else if (isInfoExpression(token)) {
            return parseInfoExpression();
        }

        throw new RuntimeException("Unexpected token: " + token.getValue());
    }

    private Expression parseInfoExpression() {
        Token token = getCurrentToken();
        String type = token.getValue();
        consume();  // Consume the type token ("ally", "opponent", or "nearby")

        if (type.equals("ally") || type.equals("opponent")) {
            return new InfoExpression(type, "");
        } else if (type.equals("nearby")) {
            Token dirToken = getCurrentToken();
            if (!isDirection(dirToken)) {
                throw new RuntimeException("Expected a valid direction after 'nearby', but found: " + dirToken.getValue());
            }
            String direction = dirToken.getValue();
            consume();  // Consume the direction token
            return new InfoExpression(type, direction);
        }

        throw new RuntimeException("Unknown info expression type: " + type);
    }

    private boolean isActionCommand(Token token) {
        return token.getType() == TokenType.DONE || token.getType() == TokenType.MOVE || token.getType() == TokenType.SHOOT;
    }

    private boolean isDirection(Token token) {
        return token.getType() == TokenType.UP || token.getType() == TokenType.DOWN ||
                token.getType() == TokenType.UPLEFT || token.getType() == TokenType.UPRIGHT ||
                token.getType() == TokenType.DOWNLEFT || token.getType() == TokenType.DOWNRIGHT;
    }

    private boolean isInfoExpression(Token token) {
        return token.getType() == TokenType.ALLY || token.getType() == TokenType.OPPONENT || token.getType() == TokenType.NEARBY;
    }
}
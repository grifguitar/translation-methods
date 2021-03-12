package sol;

import sol.unit.Terminal;
import sol.unit.Token;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public class LexicalAnalyzer {
    //fields:

    private final InputStream in;
    private int counter;
    private int currentChar;
    private Token currentToken;

    //getters and setters:

    public Token getCurrentToken() throws ParseException {
        if (this.currentToken == null) {
            throw new ParseException("the current token is not initialized", 0);
        }
        return this.currentToken;
    }

    //constructors:

    public LexicalAnalyzer(InputStream in) throws ParseException {
        this.in = in;
        this.counter = 0;
        this.currentToken = null;
        processChar();
    }

    //public methods:

    public void processToken() throws ParseException {
        this.currentToken = nextToken();
    }

    //private methods:

    private void processChar() throws ParseException {
        try {
            counter++;
            currentChar = in.read();
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), counter);
        }
    }

    private boolean isBlank(int c) {
        return Character.isSpaceChar(c) || Character.isWhitespace(c);
    }

    private Token checkOneCharToken() throws ParseException {
        String currentString = String.valueOf((char) currentChar);
        for (Terminal t : Terminal.values) {
            if (t.isEquals(currentString)) {
                return new Token(t, null);
            }
        }
        if (currentChar == -1) {
            return new Token(Terminal.DOLLAR, null);
        }
        return null;
    }

    private Token checkManyCharToken(String currentString) throws ParseException {
        for (Terminal t : Terminal.values) {
            if (t.isEquals(currentString)) {
                return new Token(t, null);
            } else {
                if (t.getSymbol() != null && t.getSymbol().startsWith("\\regex")) {
                    String regex = t.getSymbol().substring(6);
                    if (currentString.matches(regex)) {
                        return new Token(t, currentString);
                    }
                }
            }
        }
        return null;
    }

    private Token nextManyCharToken(StringBuilder currentString) throws ParseException {
        boolean wasSpace = false;
        while (isBlank(currentChar)) {
            processChar();
            wasSpace = true;
        }
        if (wasSpace || checkOneCharToken() != null) {
            Token t = checkManyCharToken(currentString.toString());
            if (t != null) {
                return t;
            } else {
                throw new ParseException("undefined token: " + currentString.toString(), counter);
            }
        } else {
            currentString.append((char) currentChar);
            processChar();
            return nextManyCharToken(currentString);
        }
    }

    private Token nextToken() throws ParseException {
        while (isBlank(currentChar)) processChar();
        Token t = checkOneCharToken();
        if (t != null) {
            processChar();
            return t;
        } else {
            StringBuilder s = new StringBuilder();
            s.append((char) currentChar);
            processChar();
            return nextManyCharToken(s);
        }
    }

}
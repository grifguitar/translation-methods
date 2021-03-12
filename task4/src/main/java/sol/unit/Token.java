package sol.unit;

import java.text.ParseException;

public class Token {
    //fields:

    private final Terminal terminal;
    private final String value;

    //getters and setters:

    public Terminal getTerminal() {
        return terminal;
    }

    public String getValue() {
        return value;
    }

    //constructors:

    public Token(Terminal terminal, String value) throws ParseException {
        if (terminal == null) {
            throw new ParseException("terminal cannot be null", 0);
        }
        this.terminal = terminal;
        this.value = value;
    }

    //overrides:

    @Override
    public String toString() {
        return "{" + getTerminal().toString() + "," + getValue() + "}";
    }

}
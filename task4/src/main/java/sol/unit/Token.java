package sol.unit;

import java.text.ParseException;

public class Token implements Unit {
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

    public Token(Token initToken) {
        this.terminal = initToken.getTerminal();
        this.value = initToken.getValue();
    }

    //overrides:

    @Override
    public String toString() {
        return "{" + getTerminal().toString() + "," + getValue() + "}";
    }

}
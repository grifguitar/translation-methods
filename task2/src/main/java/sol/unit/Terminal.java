package sol.unit;

public enum Terminal implements Unit {
    LPAREN("("),
    RPAREN(")"),
    AND("and"),
    OR("or"),
    XOR("xor"),
    NOT("not"),
    VAR("\\var"),
    EPS("\\eps"),
    DOLLAR("\\dollar");

    //fields:

    private final String symbol;

    //getters and setters:

    public String getSymbol() {
        return symbol;
    }

    //constructors:

    Terminal(String symbol) {
        this.symbol = symbol;
    }

    //public methods:

    public boolean isEquals(String symbol) {
        return getSymbol().equals(symbol);
    }

    //overrides:

    @Override
    public String toString() {
        return this.name();
    }

}
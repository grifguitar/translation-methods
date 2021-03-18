package sol.unit;

import sol.util.FastScanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;

public class Terminal implements Unit {
    //static:

    public static final HashSet<Terminal> values = new HashSet<>();

    public static final Terminal EPS = new Terminal("EPS", "\\eps");
    public static final Terminal DOLLAR = new Terminal("DOLLAR", "\\dollar");

    static {
        try {
            FastScanner in = new FastScanner(new FileInputStream("src/main/java/grammar.gk"));
            boolean isAccess = false;
            for (String line = in.nextLine(); line != null; line = in.nextLine()) {
                if (line.startsWith("-----TERMINAL")) {
                    isAccess = true;
                    continue;
                }
                if (isAccess && line.startsWith("-----")) {
                    break;
                }
                if (isAccess) {
                    String[] parts = line.split(":");
                    if (parts.length != 2) {
                        throw new RuntimeException("wrong file format 'grammar.gk', section 'TERMINAL', line: \""
                                + line + "\"");
                    }
                    if (parts[1].charAt(0) != '\'' || parts[1].charAt(parts[1].length() - 1) != '\'') {
                        throw new RuntimeException("wrong file format 'grammar.gk', section 'TERMINAL', line: \""
                                + line + "\"");
                    }
                    for (int i = 0; i < parts[0].length(); i++) {
                        if (!(parts[0].charAt(i) >= 'A' && parts[0].charAt(i) <= 'Z')) {
                            throw new RuntimeException("wrong file format 'grammar.gk', section 'TERMINAL', line: \""
                                    + line + "\"");
                        }
                    }
                    for (int i = 1; i < parts[1].length() - 1; i++) {
                        if (parts[1].charAt(i) == '\'') {
                            throw new RuntimeException("wrong file format 'grammar.gk', section 'TERMINAL', line: \""
                                    + line + "\"");
                        }
                    }
                    new Terminal(parts[0], parts[1].substring(1, parts[1].length() - 1));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //fields:

    private final String name;
    private final String symbol;

    //getters and setters:

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    //constructors:

    public Terminal(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
        Terminal.values.add(this);
    }

    //public methods:

    public boolean isEquals(String symbol) {
        return getSymbol().equals(symbol);
    }

    //overrides:

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) {
            return false;
        }
        if (ob instanceof Terminal) {
            if (this.hashCode() != ob.hashCode()) {
                return false;
            }
            return this.toString().equals(ob.toString());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
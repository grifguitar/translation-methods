package sol.unit;

import sol.attribute.Attribute;
import sol.util.FastScanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.HashMap;

public class State implements Unit {
    //static:

    public static final HashMap<String, State> values = new HashMap<>();

    static {
        try {
            FastScanner in = new FastScanner(new FileInputStream("src/main/java/grammar.gk"));
            boolean isAccess = false;
            for (String line = in.nextLine(); line != null; line = in.nextLine()) {
                if (line.startsWith("-----STATE")) {
                    isAccess = true;
                    continue;
                }
                if (isAccess && line.startsWith("-----")) {
                    break;
                }
                if (isAccess) {
                    line = line.replaceAll("\\s", "");
                    String[] parts = line.split("\\{");
                    if (parts.length != 2) {
                        throw new RuntimeException("wrong file format 'grammar.gk', section 'STATE', line: \""
                                + line + "\"");
                    }
                    if (parts[1].charAt(parts[1].length() - 1) != '}') {
                        throw new RuntimeException("wrong file format 'grammar.gk', section 'STATE', line: \""
                                + line + "\"");
                    }
                    for (int i = 0; i < parts[1].length() - 1; i++) {
                        if (parts[1].charAt(i) == '}') {
                            throw new RuntimeException("wrong file format 'grammar.gk', section 'STATE', line: \""
                                    + line + "\"");
                        }
                    }
                    String stateName = parts[0];
                    String attributes = parts[1].substring(0, parts[1].length() - 1);
                    try {
                        new State(stateName, attributes);
                    } catch (ParseException e) {
                        throw new RuntimeException("wrong file format 'grammar.gk', section 'STATE', line: \""
                                + line + "\"");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //fields:

    private final String name;

    public final HashMap<String, Attribute> synthesizedAttributes = new HashMap<>();

    public final HashMap<String, Attribute> inheritedAttributes = new HashMap<>();

    //getters and setters:

    public String getName() {
        return name;
    }

    //constructors:

    public State(String name, String attributes) throws ParseException {
        String[] allAttributes = attributes.split(",");
        for (String attr : allAttributes) {
            String[] parts = attr.split(":");
            if (parts.length != 3 || parts[0] == null || parts[0].isEmpty()) {
                throw new ParseException("invalid attribute format", 0);
            }
            if ("synth".equals(parts[2])) {
                this.synthesizedAttributes.put(parts[0], Attribute.create(parts[1]));
                continue;
            }
            if ("inherit".equals(parts[2])) {
                this.inheritedAttributes.put(parts[0], Attribute.create(parts[1]));
                continue;
            }
            throw new ParseException("invalid attribute format", 0);
        }
        if (name == null || name.length() == 0) {
            throw new ParseException("cannot create State from empty string", 0);
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if ((c < 'A' || c > 'Z') && c != '\'') {
                throw new ParseException("unexpected character on State creation from string: " + name, i);
            }
        }
        this.name = name;
        State.values.put(name, this);
    }

    public State(String name) throws ParseException {
        if (State.values.containsKey(name)) {
            State initState = State.values.get(name);
            this.synthesizedAttributes.putAll(initState.synthesizedAttributes);
            this.inheritedAttributes.putAll(initState.inheritedAttributes);
            this.name = initState.name;
        } else {
            throw new ParseException("trying to create an unresolved State from string: " + name, 0);
        }
    }

    //overrides:

    @Override
    public String toString() {
        return "~" + getName();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) {
            return false;
        }
        if (ob instanceof State) {
            if (this.hashCode() != ob.hashCode()) {
                return false;
            }
            return this.toString().equals(ob.toString());
        }
        return false;
    }

}
package sol.unit;

import java.text.ParseException;

public class State implements Unit {
    //fields:

    private final String name;

    //getters and setters:

    public String getName() {
        return name;
    }

    //constructors:

    public State(String name) throws ParseException {
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
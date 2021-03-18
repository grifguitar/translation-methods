package sol.unit;

import sol.translating.AttributeName;
import sol.translating.AttributeOrOperation;
import sol.translating.Operation;
import sol.translating.attribute.StringAttribute;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TranslatingSymbol implements Unit {
    //fields:

    private final String actionString;

    private final AttributeName leftAttr;

    private final List<AttributeOrOperation> list;

    //getters and setters:

    public AttributeName getLeftAttr() {
        return leftAttr;
    }

    public List<AttributeOrOperation> getList() {
        return list;
    }

    //constructors:

    public TranslatingSymbol(String action) throws ParseException, IllegalArgumentException {
        if (action == null || action.length() == 0) {
            throw new ParseException("cannot create TranslatingSymbol from empty string", 0);
        }
        if (action.charAt(0) == '{' && action.charAt(action.length() - 1) == '}') {
            this.actionString = action.substring(1, action.length() - 1);
        } else {
            throw new IllegalArgumentException("TranslatingSymbol: expected format \"{something}\" for string: "
                    + action);
        }

        String[] parts = actionString.split(":=");
        if (parts.length != 2) {
            throw new ParseException("TranslatingSymbol: expected format \"expr:=expr\" for string: "
                    + actionString, 0);
        }

        this.leftAttr = getAttributeName(parts[0]);

        String listString;
        if (parts[1].charAt(0) == '[' && parts[1].charAt(parts[1].length() - 1) == ']') {
            listString = parts[1].substring(1, parts[1].length() - 1);
        } else {
            throw new ParseException("TranslatingSymbol: expected format \"expr:=[expr]\" for string: "
                    + actionString, 0);
        }

        this.list = new ArrayList<>();

        for (String elem : listString.split(",")) {
            boolean isOp = false;
            for (Operation op : Operation.values) {
                if (op.symbol.equals(elem)) {
                    this.list.add(op);
                    isOp = true;
                    break;
                }
            }
            if (isOp) {
                continue;
            }
            try {
                this.list.add(getAttributeName(elem));
            } catch (ParseException e) {
                this.list.add(new StringAttribute(elem));
                //throw new RuntimeException(e.getMessage());
            }
        }

    }

    //private methods:

    private AttributeName getAttributeName(String stringName) throws ParseException {
        String[] parts = stringName.split("\\.");
        if (parts.length != 2) {
            throw new ParseException("TranslatingSymbol: expected format \"State.Attribute\" for expr in string: "
                    + actionString, 0);
        }

        String attrName = parts[1];

        String[] extendState = parts[0].split("_");
        if (extendState.length != 2) {
            throw new ParseException("TranslatingSymbol: expected format \"State_num.Attribute\" for expr in string: "
                    + actionString, 0);
        }

        try {
            return new AttributeName(new State(extendState[0]), Integer.parseInt(extendState[1]), attrName);
        } catch (NumberFormatException | ParseException e) {
            for (Terminal t : Terminal.values) {
                if (t.getName().equals(extendState[0])) {
                    return new AttributeName(t, Integer.parseInt(extendState[1]), attrName);
                }
            }
            throw new ParseException("TranslatingSymbol: incorrect expr: " + e.getMessage() + "; in string: "
                    + actionString, 0);
        }
    }

    //overrides:

    @Override
    public String toString() {
        return "{" + actionString + "}";
    }
}

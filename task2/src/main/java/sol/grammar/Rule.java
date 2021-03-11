package sol.grammar;

import sol.unit.State;
import sol.unit.Terminal;
import sol.unit.Unit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Rule {
    //fields:

    private final State fromState;
    private final List<Unit> toUnits;

    //getters and setters:

    public State getFromState() {
        return fromState;
    }

    public List<Unit> getToUnits() {
        return toUnits;
    }

    //constructors:

    public Rule(String inputString) throws ParseException {
        this.toUnits = new ArrayList<>();
        String[] partsOfRule = inputString.split("->");
        if (partsOfRule.length < 2) {
            throw new ParseException("not found symbol \"->\" in string: " + inputString, 0);
        }
        this.fromState = new State(partsOfRule[0].split("\\s")[0]);
        String[] parts = partsOfRule[1].split("\\s");
        for (String currPart : parts) {
            if (currPart.isEmpty()) {
                continue;
            }
            boolean isTerminal = false;
            for (Terminal t : Terminal.values) {
                if (t.getName().equals(currPart)) {
                    this.toUnits.add(t);
                    isTerminal = true;
                    break;
                }
            }
            if (!isTerminal) {
                this.toUnits.add(new State(currPart));
            }
        }
        if (this.toUnits.size() == 0) {
            throw new ParseException("the second part of the rule was not found in string: " + inputString, 0);
        }
    }

    //overrides:

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getFromState().toString());
        sb.append(" ->");
        for (Unit unit : toUnits) {
            sb.append(" ");
            sb.append(unit.toString());
        }
        return sb.toString();
    }

}
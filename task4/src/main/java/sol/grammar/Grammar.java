package sol.grammar;

import sol.unit.State;
import sol.unit.Terminal;
import sol.unit.TranslatingSymbol;
import sol.unit.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Grammar {
    //fields:

    private final HashMap<State, List<Rule>> allRules;
    private final HashSet<State> allStates;
    private State startState;
    private HashMap<State, HashSet<Terminal>> FIRST;
    private HashMap<State, HashSet<Terminal>> FOLLOW;

    //getters and setters:

    public HashMap<State, List<Rule>> getAllRules() {
        return allRules;
    }

    public HashSet<State> getAllStates() {
        return allStates;
    }

    public State getStartState() {
        return startState;
    }

    public HashMap<State, HashSet<Terminal>> getFIRST() {
        return FIRST;
    }

    public HashMap<State, HashSet<Terminal>> getFOLLOW() {
        return FOLLOW;
    }

    //constructors:

    public Grammar() {
        allRules = new HashMap<>();
        allStates = new HashSet<>();
        startState = null;
        FIRST = null;
        FOLLOW = null;
    }

    //public methods:

    public void addRule(Rule rule) {
        if (!allRules.containsKey(rule.getFromState())) {
            allRules.put(rule.getFromState(), new ArrayList<>());
        }
        allRules.get(rule.getFromState()).add(rule);

        allStates.add(rule.getFromState());
        for (Unit unit : rule.getToUnits()) {
            if (unit instanceof State) {
                allStates.add((State) unit);
            }
        }
    }

    public HashSet<Terminal> takeFIRST1(Rule rule) {
        HashSet<Terminal> res = new HashSet<>(calcFIRST(rule.getToUnits()));
        if (res.contains(Terminal.EPS)) {
            res.remove(Terminal.EPS);
            res.addAll(this.FOLLOW.get(rule.getFromState()));
        }
        return res;
    }

    public void initializeStartState(State startState) {
        this.startState = startState;
    }

    public void initializeFIRST() {
        this.FIRST = new HashMap<>();
        for (State state : this.getAllStates()) {
            this.FIRST.put(state, new HashSet<>());
        }

        AtomicReference<Boolean> changed = new AtomicReference<>(true);
        while (changed.get()) {
            changed.set(false);
            this.getAllRules().forEach(((state, rules) -> {
                HashSet<Unit> oldSet = new HashSet<>(this.FIRST.get(state));
                for (Rule rule : rules) {
                    this.FIRST.get(state).addAll(calcFIRST(rule.getToUnits()));
                }
                if (!oldSet.equals(this.FIRST.get(state))) {
                    changed.set(true);
                }
            }));
        }
    }

    public void initializeFOLLOW() {
        this.FOLLOW = new HashMap<>();
        for (State state : this.getAllStates()) {
            this.FOLLOW.put(state, new HashSet<>());
        }

        if (this.startState != null && this.FOLLOW.containsKey(this.startState)) {
            this.FOLLOW.get(this.startState).add(Terminal.DOLLAR);
        } else {
            throw new RuntimeException("unexpected start state when calculating FOLLOW");
        }

        AtomicReference<Boolean> changed = new AtomicReference<>(true);
        while (changed.get()) {
            changed.set(false);
            this.getAllRules().forEach(((stateA, rules) -> {
                for (Rule rule : rules) {
                    List<Unit> units = rule.getToUnits();
                    for (int i = 0; i < units.size(); i++) {
                        if (units.get(i) instanceof State) {
                            State stateB = (State) units.get(i);
                            HashSet<Unit> oldSet = new HashSet<>(this.FOLLOW.get(stateB));
                            HashSet<Terminal> res = new HashSet<>(calcFIRST(units.subList(i + 1, units.size())));
                            if (res.contains(Terminal.EPS)) {
                                res.remove(Terminal.EPS);
                                this.FOLLOW.get(stateB).addAll(this.FOLLOW.get(stateA));
                            }
                            this.FOLLOW.get(stateB).addAll(res);
                            if (!oldSet.equals(this.FOLLOW.get(stateB))) {
                                changed.set(true);
                            }
                        }
                    }
                }
            }));
        }
    }

    public boolean checkOnLL1() {
        AtomicReference<Boolean> res = new AtomicReference<>(true);

        this.getAllRules().forEach(((state, rules) -> {
            HashSet<Terminal> viewed = new HashSet<>();

            for (Rule rule : rules) {
                HashSet<Terminal> FIRST1 = this.takeFIRST1(rule);

                for (Terminal t : FIRST1) {
                    if (!viewed.contains(t)) {
                        viewed.add(t);
                    } else {
                        res.set(false);
                    }
                }
            }
        }));

        return res.get();
    }

    public String getStringByFIRST() {
        StringBuilder sb = new StringBuilder();
        getFIRST().forEach(((state, terminals) -> {
            String line = state.toString() + " : " + terminals.toString() + "\n";
            sb.append(line);
        }));
        return sb.toString();
    }

    public String getStringByFOLLOW() {
        StringBuilder sb = new StringBuilder();
        getFOLLOW().forEach(((state, terminals) -> {
            String line = state.toString() + " : " + terminals.toString() + "\n";
            sb.append(line);
        }));
        return sb.toString();
    }

    public String getStringByFIRST1() {
        StringBuilder sb = new StringBuilder();
        getAllRules().forEach(((state, rules) -> {
            for (Rule rule : rules) {
                String line = "( " + rule.toString() + " ) : " + takeFIRST1(rule).toString() + "\n";
                sb.append(line);
            }
        }));
        return sb.toString();
    }

    //private methods:

    private HashSet<Terminal> calcFIRST(List<Unit> list) {
        if (list.size() == 0) {
            HashSet<Terminal> res = new HashSet<>();
            res.add(Terminal.EPS);
            return res;
        }
        Unit unit0 = list.get(0);
        if (unit0 instanceof Terminal) {
            HashSet<Terminal> res = new HashSet<>();
            res.add((Terminal) unit0);
            return res;
        }
        if (unit0 instanceof State) {
            HashSet<Terminal> res = new HashSet<>(this.FIRST.get(unit0));
            if (res.contains(Terminal.EPS)) {
                res.remove(Terminal.EPS);
                res.addAll(calcFIRST(list.subList(1, list.size())));
            }
            return res;
        }
        if (unit0 instanceof TranslatingSymbol) {
            return calcFIRST(list.subList(1, list.size()));
        }
        throw new RuntimeException("unexpected case when calculating FIRST");
    }

    //overrides:

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        getAllRules().forEach((state, rules) -> {
            for (Rule rule : rules) {
                String line = state.toString() + " : " + rule.toString() + "\n";
                sb.append(line);
            }
        });
        return sb.toString();
    }

}
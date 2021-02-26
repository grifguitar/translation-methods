package sol;

import sol.grammar.Grammar;
import sol.grammar.Rule;
import sol.unit.State;
import sol.unit.Terminal;
import sol.unit.Unit;

import java.io.InputStream;
import java.text.ParseException;
import java.util.HashSet;

public class Parser {
    //fields:

    private final Grammar grammar;
    private LexicalAnalyzer lexicalAnalyzer;

    //getters and setters:

    public LexicalAnalyzer getLexicalAnalyzer() {
        return lexicalAnalyzer;
    }

    //constructors:

    public Parser(Grammar grammar) throws ParseException {
        this.grammar = grammar;
        if (!this.grammar.checkOnLL1()) {
            throw new ParseException("grammar is not LL1", 0);
        }
    }

    //public methods:

    public Node parse(InputStream inputStream) throws ParseException {
        lexicalAnalyzer = new LexicalAnalyzer(inputStream);
        lexicalAnalyzer.processToken();
        return parseByState(grammar.getStartState());
    }

    //private methods:

    private Node parseByState(State state) throws ParseException {
        HashSet<Terminal> viewed = new HashSet<>();
        Node res = new Node(state);

        for (Rule rule : grammar.getAllRules().get(state)) {
            HashSet<Terminal> FIRST1 = grammar.takeFIRST1(rule);
            viewed.addAll(FIRST1);

            if (FIRST1.contains(lexicalAnalyzer.getCurrentToken().getTerminal())) {
                for (Unit unit : rule.getToUnits()) {
                    if (unit instanceof State) {
                        Node node = parseByState((State) unit);
                        res.addChild(node);
                        continue;
                    }
                    if (unit instanceof Terminal) {
                        Terminal t = (Terminal) unit;
                        if (t.equals(Terminal.EPS)) {
                            continue;
                        }
                        if (t.equals(lexicalAnalyzer.getCurrentToken().getTerminal())) {
                            res.addChild(new Node(t));
                            lexicalAnalyzer.processToken();
                            continue;
                        } else {
                            throw new ParseException("expected " + t.toString() +
                                    ", found " + lexicalAnalyzer.getCurrentToken().toString(), 0);
                        }
                    }
                    throw new RuntimeException("unexpected case in parseByState: " + state.toString());
                }

                return res;
            }
        }

        throw new ParseException("expected something from " + viewed.toString() +
                ", found " + lexicalAnalyzer.getCurrentToken().toString(), 0);
    }

}
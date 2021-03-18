package sol;

import sol.translating.attribute.Attribute;
import sol.translating.attribute.StringAttribute;
import sol.grammar.Grammar;
import sol.grammar.Rule;
import sol.unit.*;
import sol.translating.AttributeName;
import sol.translating.AttributeOrOperation;
import sol.translating.Operation;
import sol.unit.TranslatingSymbol;

import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

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
                //нашли правило, по которому раскрывать

                HashMap<String, Attribute> allAttributes = new HashMap<>();

                ((State) res.getRoot()).inheritedAttributes.forEach((name, value) -> {
                    String fullName = calcName(((State) res.getRoot()).getName(), 0, name);
                    Attribute newValue = value.getCopy();
                    allAttributes.put(fullName, newValue);
                });

                int num = 0;
                for (Unit unit : rule.getToUnits()) {
                    num++;
                    if (unit instanceof State) {
                        State st = (State) unit;
                        int finalNum = num;

                        st.inheritedAttributes.forEach((name, value) -> {
                            String fullName = calcName(st.getName(), finalNum, name);
                            if (allAttributes.containsKey(fullName)) {
                                Attribute newValue = allAttributes.get(fullName).getCopy();
                                st.inheritedAttributes.put(name, newValue);
                            } else {
                                throw new RuntimeException("State; requested attribute was not found: "
                                        + fullName + " in parseByState: " + state.toString());
                            }
                        });

                        Node node = parseByState(st);

                        ((State) node.getRoot()).synthesizedAttributes.forEach((name, value) -> {
                            Attribute newValue = value.getCopy();
                            allAttributes.put(calcName(((State) node.getRoot()).getName(), finalNum, name), newValue);
                        });

                        res.addChild(node);
                        continue;
                    }
                    if (unit instanceof Terminal) {
                        Terminal t = (Terminal) unit;
                        if (t.equals(Terminal.EPS)) {
                            continue;
                        }
                        if (t.equals(lexicalAnalyzer.getCurrentToken().getTerminal())) {
                            Node node = new Node(lexicalAnalyzer.getCurrentToken());

                            Token token = (Token) node.getRoot();

                            Attribute newValue = (new StringAttribute(token.getValue())).getCopy();
                            allAttributes.put(calcName(token.getTerminal().getName(), num, "VALUE"),
                                    newValue);

                            res.addChild(node);
                            lexicalAnalyzer.processToken();
                            continue;
                        } else {
                            throw new ParseException("expected " + t.toString() +
                                    ", found " + lexicalAnalyzer.getCurrentToken().toString(), 0);
                        }
                    }
                    if (unit instanceof TranslatingSymbol) {
                        TranslatingSymbol ts = (TranslatingSymbol) unit;

                        Deque<Attribute> deque = new ArrayDeque<>();

                        for (AttributeOrOperation elem : ts.getList()) {
                            if (elem instanceof AttributeName) {
                                String fullName = calcName((AttributeName) elem);
                                if (allAttributes.containsKey(fullName)) {
                                    Attribute newValue = allAttributes.get(fullName).getCopy();
                                    deque.addLast(newValue);
                                } else {
                                    throw new RuntimeException("TranslatingSymbol; requested attribute was not found: "
                                            + fullName + " in parseByState: " + state.toString());
                                }
                                continue;
                            }
                            if (elem instanceof Attribute) {
                                Attribute newValue = ((Attribute) elem).getCopy();
                                deque.addLast(newValue);
                                continue;
                            }
                            if (elem instanceof Operation) {
                                Operation op = (Operation) elem;
                                if (op.isBinary) {
                                    try {
                                        Attribute x = deque.removeLast();
                                        Attribute y = deque.removeLast();
                                        deque.addLast(op.apply(y, x));
                                    } catch (NoSuchElementException e) {
                                        throw new RuntimeException("incorrect calculations in translating symbol");
                                    }
                                } else {
                                    try {
                                        Attribute x = deque.removeLast();
                                        deque.addLast(op.apply(x, null));
                                    } catch (NoSuchElementException e) {
                                        throw new RuntimeException("incorrect calculations in translating symbol");
                                    }
                                }
                                continue;
                            }
                            throw new RuntimeException("TranslatingSymbol; unexpected case in parseByState: "
                                    + state.toString());
                        }

                        if (deque.size() != 1) {
                            throw new RuntimeException("incorrect calculations in translating symbol");
                        }

                        Attribute newValue = deque.getLast().getCopy();
                        allAttributes.put(calcName(ts.getLeftAttr()), newValue);
                        continue;
                    }
                    throw new RuntimeException("unexpected case in parseByState: " + state.toString() + " " + unit);
                }

                ((State) res.getRoot()).synthesizedAttributes.forEach((name, value) -> {
                    String fullName = calcName(((State) res.getRoot()).getName(), 0, name);
                    if (allAttributes.containsKey(fullName)) {
                        Attribute newValue = allAttributes.get(fullName).getCopy();
                        ((State) res.getRoot()).synthesizedAttributes.put(name, newValue);
                    } else {
                        throw new RuntimeException("requested attribute was not found: "
                                + fullName + " in parseByState: " + state.toString());
                    }
                });

                return res;
            }
        }

        throw new ParseException("expected something from " + viewed.toString() +
                ", found " + lexicalAnalyzer.getCurrentToken().toString(), 0);
    }

    private static String calcName(String name, Integer ind, String attributeName) {
        return name + "_" + ind + "." + attributeName;
    }

    private static String calcName(AttributeName attrName) {
        if (attrName.unit instanceof State) {
            return calcName(((State) attrName.unit).getName(), attrName.num, attrName.attribute);
        }
        if (attrName.unit instanceof Terminal) {
            return calcName(((Terminal) attrName.unit).getName(), attrName.num, attrName.attribute);
        }
        throw new RuntimeException("unexpected case in Parser, calcName");
    }
}
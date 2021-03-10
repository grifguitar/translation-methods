package sol;

import sol.grammar.Grammar;
import sol.grammar.Rule;

import java.text.ParseException;

public enum InputGrammar {
    GRAMMAR1("""
            E  -> T E'\s
            E' -> OR T E'\s
            E' -> XOR T E'\s
            E' -> EPS\s
            T  -> F T'\s
            T' -> AND F T'\s
            T' -> EPS\s
            F  -> NOT U\s
            F  -> U\s
            U  -> VAR\s
            U  -> LPAREN E RPAREN\s
            """);

    //fields:

    private final Grammar grammar;

    //getters and setters:

    public Grammar getGrammar() {
        return grammar;
    }

    //constructors:

    InputGrammar(String allStrings) {
        this.grammar = new Grammar();

        String[] lines = allStrings.split("\\n");
        for (String line : lines) {
            try {
                Rule rule = new Rule(line);
                this.grammar.addRule(rule);

                if (this.grammar.getStartState() == null) {
                    this.grammar.initializeStartState(rule.getFromState());
                }
            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        this.grammar.initializeFIRST();

        this.grammar.initializeFOLLOW();
    }

}
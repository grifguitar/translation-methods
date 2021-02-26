package sol;

import sol.grammar.Grammar;
import sol.grammar.Rule;

import java.text.ParseException;

public enum InputGrammar {
    GRAMMAR1("""
            E  -> T E'\s
            E' -> or T E'\s
            E' -> xor T E'\s
            E' -> \\eps\s
            T  -> F T'\s
            T' -> and F T'\s
            T' -> \\eps\s
            F  -> not U\s
            F  -> U\s
            U  -> \\var\s
            U  -> ( E )\s
            """);
//    GRAMMAR1("""
//            E  -> T E'\s
//            E' -> or T E'\s
//            E' -> \\eps\s
//            T  -> F T'\s
//            T' -> and F T'\s
//            T' -> \\eps\s
//            F  -> \\var\s
//            F  -> ( E )\s
//            """);
//    GRAMMAR1("""
//            S  -> ( S' ) S'\s
//            S' -> S\s
//            S' -> \\eps\s
//            """);

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
package sol;

import sol.grammar.Grammar;
import sol.grammar.Rule;
import sol.util.FastScanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class InputGrammar {
    //static:

    public static final InputGrammar GRAMMAR1;

    static {
        List<String> list = new ArrayList<>();
        try {
            FastScanner in = new FastScanner(new FileInputStream("src/main/java/grammar.gk"));
            boolean isAccess = false;
            for (String line = in.nextLine(); line != null; line = in.nextLine()) {
                if (line.startsWith("-----GRAMMAR")) {
                    isAccess = true;
                    continue;
                }
                if (isAccess && line.startsWith("-----")) {
                    break;
                }
                if (isAccess) {
                    list.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        GRAMMAR1 = new InputGrammar(list);
    }

    //fields:

    private final Grammar grammar;

    //getters and setters:

    public Grammar getGrammar() {
        return grammar;
    }

    //constructors:

    public InputGrammar(List<String> lines) {
        this.grammar = new Grammar();

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
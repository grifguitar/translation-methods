import sol.*;
import sol.grammar.Grammar;
import sol.unit.Terminal;

import java.io.*;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) {
        try {
            Writer out = new PrintWriter(new FileOutputStream("output.dot"));
            InputStream in = System.in;
            Node node = solve(in);
            node.createDotFile(out, "Lab2");
            out.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Node solve(InputStream inputStream) throws ParseException, IOException {
        printGrammarInfo(InputGrammar.GRAMMAR1.getGrammar());

        Parser parser = new Parser(InputGrammar.GRAMMAR1.getGrammar());
        Node node = parser.parse(inputStream);

        System.out.println(node.toString());

        if (parser.getLexicalAnalyzer().getCurrentToken().getTerminal() != Terminal.DOLLAR) {
            throw new ParseException("expected " + Terminal.DOLLAR.toString() + ", found " +
                    parser.getLexicalAnalyzer().getCurrentToken().toString(), 0);
        }

        return node;
    }

    private static void printGrammarInfo(Grammar grammar) {
        System.out.println("------PARSED-GRAMMAR------");
        System.out.println(grammar.toString());

        System.out.println("----------FIRST-----------");
        System.out.println(grammar.getStringByFIRST());

        System.out.println("----------FOLLOW----------");
        System.out.println(grammar.getStringByFOLLOW());

        System.out.println("-------START-STATE--------");
        System.out.println(grammar.getStartState().toString() + "\n");

        System.out.println("----------FIRST'----------");
        System.out.println(grammar.getStringByFIRST1());
    }
}
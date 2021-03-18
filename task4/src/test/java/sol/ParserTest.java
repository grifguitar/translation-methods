package sol;

import org.junit.Assert;
import org.junit.Test;
import sol.unit.Terminal;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class ParserTest {

    @Test
    public void test1() {
        String text = "2 * 3";

        String expected = "~E:[" +
                "~T:[" +
                "~F:[~U:[{VAR,2}:[]]]" +
                ", " +
                "~T':[" +
                "{MUL,null}:[]" +
                ", " +
                "~F:[~U:[{VAR,3}:[]]]" +
                ", " +
                "~T':[]" +
                "]" +
                "]" +
                ", " +
                "~E':[]" +
                "]";

        try {
            Parser parser = new Parser(InputGrammar.GRAMMAR1.getGrammar());
            Node node = parser.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));

            if (parser.getLexicalAnalyzer().getCurrentToken().getTerminal() != Terminal.DOLLAR) {
                throw new ParseException("expected " + Terminal.DOLLAR.toString() + ", found " +
                        parser.getLexicalAnalyzer().getCurrentToken().toString(), 0);
            }

            Assert.assertEquals(expected, node.toString());
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test2() {
        String text = "6 / 5 - 4 * (2 + 3)";

        String expected = "~E:[~T:[~F:[~U:[{VAR,6}:[]]]" +
                ", " +
                "~T':[{DIV,null}:[]" +
                ", " +
                "~F:[~U:[{VAR,5}:[]]]" +
                ", " +
                "~T':[]]]" +
                ", " +
                "~E':[{SUB,null}:[]" +
                ", " +
                "~T:[~F:[~U:[{VAR,4}:[]]]" +
                ", " +
                "~T':[{MUL,null}:[]" +
                ", " +
                "~F:[~U:[{LPAREN,null}:[]" +
                ", " +
                "~E:[~T:[~F:[~U:[{VAR,2}:[]]]" +
                ", " +
                "~T':[]]" +
                ", " +
                "~E':[{ADD,null}:[]" +
                ", " +
                "~T:[~F:[~U:[{VAR,3}:[]]]" +
                ", " +
                "~T':[]]" +
                ", " +
                "~E':[]]]" +
                ", " +
                "{RPAREN,null}:[]]]" +
                ", " +
                "~T':[]]]" +
                ", " +
                "~E':[]]]";

        try {
            Parser parser = new Parser(InputGrammar.GRAMMAR1.getGrammar());
            Node node = parser.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));

            if (parser.getLexicalAnalyzer().getCurrentToken().getTerminal() != Terminal.DOLLAR) {
                throw new ParseException("expected " + Terminal.DOLLAR.toString() + ", found " +
                        parser.getLexicalAnalyzer().getCurrentToken().toString(), 0);
            }

            Assert.assertEquals(expected, node.toString());
        } catch (ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void exceptTest2() {
        String text = "(1 + 0))";

        String expectedException = "expected DOLLAR, found {RPAREN,null}";

        try {
            Parser parser = new Parser(InputGrammar.GRAMMAR1.getGrammar());
            parser.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));

            if (parser.getLexicalAnalyzer().getCurrentToken().getTerminal() != Terminal.DOLLAR) {
                throw new ParseException("expected " + Terminal.DOLLAR.toString() + ", found " +
                        parser.getLexicalAnalyzer().getCurrentToken().toString(), 0);
            }

            Assert.fail("there was no exception");
        } catch (ParseException e) {
            Assert.assertEquals(expectedException, e.getMessage());
        }
    }
}
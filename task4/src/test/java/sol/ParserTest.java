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
        String text = "a and b";

        String expected = "~E:[" +
                "~T:[" +
                "~F:[~U:[VAR:[]]]" +
                ", " +
                "~T':[" +
                "AND:[]" +
                ", " +
                "~F:[~U:[VAR:[]]]" +
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
    public void exceptTest2() {
        String text = "(a and b))";

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
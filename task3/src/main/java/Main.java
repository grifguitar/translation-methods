import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PrefixExpressionLexer lexer = new PrefixExpressionLexer(CharStreams.fromStream(System.in));
        PrefixExpressionParser parser = new PrefixExpressionParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.program();
        MyVisitor visitor = new MyVisitor();
        String result = visitor.visit(tree);
        System.out.println(result);
    }

    public static class MyVisitor extends PrefixExpressionBaseVisitor<String> {
        @Override
        public String visitProgram(PrefixExpressionParser.ProgramContext ctx) {
            return super.visitProgram(ctx);
        }

        @Override
        public String visitStatement(PrefixExpressionParser.StatementContext ctx) {
            return "\n" + super.visitStatement(ctx);
        }

        @Override
        public String visitIfStatement(PrefixExpressionParser.IfStatementContext ctx) {
            return "if " + visit(ctx.getChild(1)) + " {" +
                    visit(ctx.getChild(2)) + "\n" +
                    "} else {" +
                    visit(ctx.getChild(3)) + "\n" +
                    "}";
        }

        @Override
        public String visitWhileStatement(PrefixExpressionParser.WhileStatementContext ctx) {
            return "while (" + visit(ctx.getChild(1)) + ") {" +
                    visit(ctx.getChild(2)) + "\n" +
                    "}";
        }

        @Override
        public String visitAssignmentStatement(PrefixExpressionParser.AssignmentStatementContext ctx) {
            return "int " + visit(ctx.getChild(1)) + " = " + visit(ctx.getChild(2)) + ";";
        }

        @Override
        public String visitPrintStatement(PrefixExpressionParser.PrintStatementContext ctx) {
            return "System.out.println(" + visit(ctx.getChild(1)) + ");";
        }

        @Override
        public String visitEmptyStatement(PrefixExpressionParser.EmptyStatementContext ctx) {
            return "";
        }

        @Override
        public String visitExpression(PrefixExpressionParser.ExpressionContext ctx) {
            return super.visitExpression(ctx);
        }

        @Override
        public String visitLogicalExpression(PrefixExpressionParser.LogicalExpressionContext ctx) {
            if (ctx.numCompOp() != null) {
                return "(" + visit(ctx.getChild(1)) +
                        " " + visit(ctx.getChild(0)) +
                        " " + visit(ctx.getChild(2)) + ")";
            }
            if (ctx.unLogicOp() != null) {
                return "(" + visit(ctx.getChild(0)) + visit(ctx.getChild(1)) + ")";
            }
            if (ctx.binLogicOp() != null) {
                return "(" + visit(ctx.getChild(1)) +
                        " " + visit(ctx.getChild(0)) +
                        " " + visit(ctx.getChild(2)) + ")";
            }
            return super.visitLogicalExpression(ctx);
        }

        @Override
        public String visitArithmeticExpression(PrefixExpressionParser.ArithmeticExpressionContext ctx) {
            if (ctx.unArithmOp() != null) {
                return "(" + visit(ctx.getChild(0)) + visit(ctx.getChild(1)) + ")";
            }
            if (ctx.binArithmOp() != null) {
                String ans = "(" + visit(ctx.getChild(1)) +
                        " " + visit(ctx.getChild(0)) +
                        " " + visit(ctx.getChild(2)) + ")";
                if (ctx.binArithmOp().DIV() != null) {
                    if ("0".equals(visit(ctx.getChild(2)))) {
                        throw new RuntimeException("division by zero in expression: " + ans);
                    }
                }
                return ans;
            }
            return super.visitArithmeticExpression(ctx);
        }

        @Override
        public String visitNumCompOp(PrefixExpressionParser.NumCompOpContext ctx) {
            return super.visitNumCompOp(ctx);
        }

        @Override
        public String visitUnLogicOp(PrefixExpressionParser.UnLogicOpContext ctx) {
            if (ctx.NOT() != null) {
                return "!";
            }
            return super.visitUnLogicOp(ctx);
        }

        @Override
        public String visitBinLogicOp(PrefixExpressionParser.BinLogicOpContext ctx) {
            if (ctx.AND() != null) {
                return "&&";
            }
            if (ctx.OR() != null) {
                return "||";
            }
            if (ctx.XOR() != null) {
                return "^";
            }
            return super.visitBinLogicOp(ctx);
        }

        @Override
        public String visitUnArithmOp(PrefixExpressionParser.UnArithmOpContext ctx) {
            if (ctx.NEGATE() != null) {
                return "-";
            }
            return super.visitUnArithmOp(ctx);
        }

        @Override
        public String visitBinArithmOp(PrefixExpressionParser.BinArithmOpContext ctx) {
            return super.visitBinArithmOp(ctx);
        }

        @Override
        public String visit(ParseTree tree) {
            return super.visit(tree);
        }

        @Override
        public String visitChildren(RuleNode node) {
            return super.visitChildren(node);
        }

        @Override
        public String visitTerminal(TerminalNode node) {
            if (node.toString().equals("<EOF>")) {
                return "";
            }
            return node.toString();
        }

        @Override
        public String visitErrorNode(ErrorNode node) {
            return super.visitErrorNode(node);
        }

        @Override
        protected String defaultResult() {
            return "";
        }

        @Override
        protected String aggregateResult(String aggregate, String nextResult) {
            if (aggregate.isEmpty()) {
                return nextResult;
            } else {
                return aggregate + " " + nextResult;
            }
        }

        @Override
        protected boolean shouldVisitNextChild(RuleNode node, String currentResult) {
            return super.shouldVisitNextChild(node, currentResult);
        }
    }
}
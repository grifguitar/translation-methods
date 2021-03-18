package sol;

import sol.unit.State;
import sol.unit.Unit;
import sol.unit.Token;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    //fields:

    private final Unit root;
    private final List<Node> children;

    //getters and setters:

    public Unit getRoot() {
        return root;
    }

    public List<Node> getChildren() {
        return children;
    }

    //constructors:

    public Node(State state) {
        this.root = new State(state);
        this.children = new ArrayList<>();
    }

    public Node(Token token) {
        this.root = new Token(token);
        this.children = new ArrayList<>();
    }

    //public methods:

    public void addChild(Node child) {
        children.add(child);
    }

    public void createDotFile(Writer writer, String graphName) throws IOException {
        writer.write("digraph " + graphName + " {\n");
        this.printDotFile(writer, new AtomicInteger(0));
        writer.write("}\n");
    }

    //private methods:

    private void printDotFile(Writer writer, AtomicInteger num) throws IOException {
        int currNum = num.get();
        String currSysName = "N" + currNum;
        String currUserName = root.toString();
        if (root instanceof State) {
            StringBuilder sb = new StringBuilder();
            if (!((State) root).synthesizedAttributes.isEmpty()) {
                sb.append("\\nsyn:");
                ((State) root).synthesizedAttributes.forEach((name, value) -> {
                    sb.append(" ");
                    sb.append(name);
                    sb.append("=");
                    sb.append(value);
                });
            }
            if (!((State) root).inheritedAttributes.isEmpty()) {
                sb.append("\\ninh:");
                ((State) root).inheritedAttributes.forEach((name, value) -> {
                    sb.append(" ");
                    sb.append(name);
                    sb.append("=");
                    sb.append(value);
                });
            }
            currUserName += sb.toString();
        }

        writer.write("  " + currSysName + " [shape=box, label=\"" + currUserName + "\"];\n");

        for (Node child : children) {
            int childNum = num.incrementAndGet();
            String childSysName = "N" + childNum;

            child.printDotFile(writer, num);

            writer.write("  " + currSysName + " -> " + childSysName + ";\n");
        }
    }

    //overrides:

    @Override
    public String toString() {
        return getRoot().toString() + ":" + getChildren().toString();
    }

}
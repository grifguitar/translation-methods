package sol;

import sol.unit.State;
import sol.unit.Terminal;
import sol.unit.Unit;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    //fields:

    private final Unit unit;
    private final List<Node> children;

    //getters and setters:

    public Unit getUnit() {
        return unit;
    }

    public List<Node> getChildren() {
        return children;
    }

    //constructors:

    public Node(State state) {
        this.unit = state;
        this.children = new ArrayList<>();
    }

    public Node(Terminal terminal) {
        this.unit = terminal;
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
        String currUserName = unit.toString();

        writer.write("  " + currSysName + " [label=\"" + currUserName + "\"];\n");

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
        return getUnit().toString() + ":" + getChildren().toString();
    }

}
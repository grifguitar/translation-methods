package sol.translating;

import bsh.EvalError;
import bsh.Interpreter;
import sol.translating.attribute.Attribute;
import sol.translating.attribute.BooleanAttribute;
import sol.translating.attribute.IntegerAttribute;
import sol.translating.attribute.StringAttribute;
import sol.util.FastScanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class Operation implements AttributeOrOperation {
    //static:

    public static final HashSet<Operation> values = new HashSet<>();

    static {
        List<String> list = new ArrayList<>();
        try {
            FastScanner in = new FastScanner(new FileInputStream("src/main/java/grammar.gk"));
            boolean isAccess = false;
            for (String line = in.nextLine(); line != null; line = in.nextLine()) {
                if (line.startsWith("-----OPERATION")) {
                    isAccess = true;
                    continue;
                }
                if (isAccess && line.startsWith("-----")) {
                    break;
                }
                if (isAccess && line.startsWith(";")) {
                    String firstLine = list.get(0).replaceAll("\\s", "");
                    String[] parts = firstLine.split("::");
                    if (parts.length != 2) {
                        throw new RuntimeException("wrong file format 'grammar.gk', section 'OPERATION', line: \""
                                + firstLine + "\"");
                    }

                    StringBuilder body = new StringBuilder();
                    for (int i = 1; i < list.size(); i++) {
                        body.append(list.get(i));
                    }

                    String[] types = parts[1].split("->");
                    if (types.length == 2) {
                        new Operation(parts[0], false, body.toString(), types) {
                            @Override
                            public Attribute apply(Attribute x, Attribute y) {
                                Object firstArg = Operation.tryCast(x, this.types[0]);
                                Interpreter it = new Interpreter();
                                try {
                                    it.set("firstArg", firstArg);
                                    it.eval(this.body);
                                    Object result = it.get("result");
                                    return Operation.tryCast(result, this.types[1]);
                                } catch (EvalError evalError) {
                                    throw new RuntimeException(evalError.getMessage());
                                }
                            }
                        };
                        list.clear();
                        continue;
                    }
                    if (types.length == 3) {
                        new Operation(parts[0], true, body.toString(), types) {
                            @Override
                            public Attribute apply(Attribute x, Attribute y) {
                                Object firstArg = Operation.tryCast(x, this.types[0]);
                                Object secondArg = Operation.tryCast(y, this.types[1]);
                                Interpreter it = new Interpreter();
                                try {
                                    it.set("firstArg", firstArg);
                                    it.set("secondArg", secondArg);
                                    it.eval(this.body);
                                    Object result = it.get("result");
                                    return Operation.tryCast(result, this.types[2]);
                                } catch (EvalError evalError) {
                                    throw new RuntimeException(evalError.getMessage());
                                }
                            }
                        };
                        list.clear();
                        continue;
                    }
                    throw new RuntimeException("wrong file format 'grammar.gk', section 'OPERATION', line: \""
                            + firstLine + "\"");
                }
                if (isAccess) {
                    list.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //fields:

    public final String body;
    public final String[] types;

    public final String symbol;
    public final boolean isBinary;

    //constructors:

    public Operation(String symbol, boolean isBinary, String body, String[] types) {
        this.body = body;
        this.types = types;
        this.symbol = symbol;
        this.isBinary = isBinary;
        Operation.values.add(this);
    }

    //public methods:

    public abstract Attribute apply(Attribute x, Attribute y);

    //private methods:

    private static Object tryCast(Attribute attr, String stringType) {
        if ("Integer".equals(stringType) && attr instanceof IntegerAttribute) {
            return ((IntegerAttribute) attr).value;
        } else if ("Boolean".equals(stringType) && attr instanceof BooleanAttribute) {
            return ((BooleanAttribute) attr).value;
        } else if ("String".equals(stringType) && attr instanceof StringAttribute) {
            return ((StringAttribute) attr).value;
        } else {
            throw new RuntimeException("wrong file format 'grammar.gk', section 'OPERATION'," +
                    " undefined type: \"" + stringType + "\"");
        }
    }

    private static Attribute tryCast(Object result, String stringType) {
        if ("Integer".equals(stringType)) {
            return new IntegerAttribute((int) result);
        } else if ("Boolean".equals(stringType)) {
            return new BooleanAttribute((boolean) result);
        } else if ("String".equals(stringType)) {
            return new StringAttribute((String) result);
        } else {
            throw new RuntimeException("wrong file format 'grammar.gk', section 'OPERATION'," +
                    " undefined type: \"" + stringType + "\"");
        }
    }
}

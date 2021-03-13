package sol.attribute;

import java.text.ParseException;

public interface Attribute {
    static Attribute create(String typeName) throws ParseException {
        if ("Boolean".equals(typeName)) {
            return new BooleanAttribute();
        }
        if ("Character".equals(typeName)) {
            return new CharacterAttribute();
        }
        if ("Double".equals(typeName)) {
            return new DoubleAttribute();
        }
        if ("Integer".equals(typeName)) {
            return new IntegerAttribute();
        }
        if ("Long".equals(typeName)) {
            return new LongAttribute();
        }
        if ("String".equals(typeName)) {
            return new StringAttribute();
        }
        throw new ParseException("invalid attribute format", 0);
    }
}

package sol.translating.attribute;

import sol.translating.AttributeOrOperation;

public interface Attribute extends AttributeOrOperation {
    Attribute getCopy();
}

package sol.translating;

import sol.unit.Unit;

public class AttributeName implements AttributeOrOperation {
    public Unit unit;
    public Integer num;
    public String attribute;

    public AttributeName(Unit unit, Integer num, String attribute) {
        this.unit = unit;
        this.num = num;
        this.attribute = attribute;
    }
}

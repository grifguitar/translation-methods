package sol.translating.attribute;

public class BooleanAttribute implements Attribute {
    public final Boolean value;

    public BooleanAttribute(boolean value) {
        this.value = value;
    }

    @Override
    public Attribute getCopy() {
        return new BooleanAttribute(this.value);
    }

    @Override
    public String toString() {
        return "(bool)" + value;
    }
}

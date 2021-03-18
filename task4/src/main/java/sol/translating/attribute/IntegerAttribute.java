package sol.translating.attribute;

public class IntegerAttribute implements Attribute {
    public final Integer value;

    public IntegerAttribute(int value) {
        this.value = value;
    }

    @Override
    public Attribute getCopy() {
        return new IntegerAttribute(this.value);
    }

    @Override
    public String toString() {
        return "(int)" + value;
    }
}

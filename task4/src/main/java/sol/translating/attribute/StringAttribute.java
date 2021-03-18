package sol.translating.attribute;

public class StringAttribute implements Attribute {
    public final String value;

    public StringAttribute(String value) {
        this.value = value;
    }

    @Override
    public Attribute getCopy() {
        return new StringAttribute(this.value);
    }

    @Override
    public String toString() {
        return "(string)" + value;
    }
}

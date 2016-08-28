package lohbihler.manfred.datalog.text;

abstract public class BaseSampleTextinator<T> implements SampleTextinator<T> {
    private StringBuilder sb = new StringBuilder();

    @Override
    public boolean shouldTextinate(T o) {
        return o != null;
    }

    @Override
    public String textinate(long time, T o) {
        sb.append(time);
        append(sb, o);
        String result = sb.toString();
        sb.delete(0, sb.length());
        return result;
    }

    abstract protected void append(StringBuilder sb, T o);

    protected void append(StringBuilder sb, int i) {
        sb.append(',').append(i);
    }

    protected void append(StringBuilder sb, float f) {
        sb.append(',').append(f);
    }

    protected void append(StringBuilder sb, double d) {
        sb.append(',').append(d);
    }

    protected void append(StringBuilder sb, boolean b) {
        sb.append(',').append(b ? "T" : "F");
    }

    protected void append(StringBuilder sb, char c) {
        sb.append(',').append(c);
    }

    protected void append(StringBuilder sb, String s) {
        sb.append(',').append(s);
    }

    protected void append(StringBuilder sb, Enum<?> e) {
        sb.append(',').append(e == null ? null : e.name());
    }
}

package lohbihler.manfred.datalog.text;

public interface SampleTextinator<T> {
    boolean shouldTextinate(T o);

    String textinate(long time, T o);
}

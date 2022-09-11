package net.tarcadia.util.function;

@FunctionalInterface
public interface Classifier<T> {

    Classifier ALWAYS = o -> true;
    Classifier NEVER = o -> false;

    boolean classify(T t);
}

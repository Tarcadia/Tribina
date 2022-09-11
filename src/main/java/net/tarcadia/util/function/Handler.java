package net.tarcadia.util.function;

@FunctionalInterface
public interface Handler<T> {
    void handle(T t);
}

package net.tarcadia.util.function;

@FunctionalInterface
public interface Supplier<T> {
    T supply();
}

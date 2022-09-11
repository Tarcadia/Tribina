package net.tarcadia.util.function;

@FunctionalInterface
public interface Provider<T, P> {
    P provide(T t);
}

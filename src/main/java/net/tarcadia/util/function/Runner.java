package net.tarcadia.util.function;

@FunctionalInterface
public interface Runner extends Runnable {
    @Override
    void run();
}

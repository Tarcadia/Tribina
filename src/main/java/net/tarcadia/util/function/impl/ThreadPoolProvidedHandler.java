package net.tarcadia.util.function.impl;

import net.tarcadia.util.function.Handler;
import net.tarcadia.util.function.Provider;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolProvidedHandler<T> implements Handler<T> {

    private final String name;
    private final BlockingQueue<Handler<T>> queue = new LinkedBlockingQueue<>();
    private final Thread[] pool;
    private final Provider<T, Handler<T>> provider;

    public ThreadPoolProvidedHandler(int size, @NotNull Provider<T, Handler<T>> provider) {
        this.name = "";
        this.pool = new Thread[size];
        this.provider = provider;
        for (int i = 0; i < this.pool.length; i++) {
            pool[i] = new Thread(this::threaded);
            pool[i].start();
        }
    }

    public ThreadPoolProvidedHandler(@NotNull String name, int size, @NotNull Provider<T, Handler<T>> provider) {
        this.name = name;
        this.pool = new Thread[size];
        this.provider = provider;
        for (int i = 0; i < this.pool.length; i++) {
            pool[i] = new Thread(this::threaded);
            pool[i].start();
        }
    }

    public void interrupt() {
        for (Thread thread : this.pool) {
            thread.interrupt();
        }
    }

    @Override
    public void handle(T t) {
        Handler<T> handler = o -> this.provider.provide(t).handle(t);
        this.queue.offer(handler);
    }

    private void threaded() {
        try {
            while (true) Objects.requireNonNull(this.queue.take()).handle(null);
        } catch (InterruptedException ignored) {
        }
    }
}

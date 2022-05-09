package net.tarcadia.util.function.impl;

import net.tarcadia.util.function.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolProvidedHandler<T extends Handler<U>, U> implements Handler<T> {

    private final String name;
    private final BlockingQueue<Handler<U>> queue = new LinkedBlockingQueue<>();
    private final Thread[] pool;

    public ThreadPoolProvidedHandler(int size) {
        this.name = "";
        this.pool = new Thread[size];
        for (int i = 0; i < this.pool.length; i++) {
            pool[i] = new Thread(this::threaded);
            pool[i].start();
        }
    }

    public ThreadPoolProvidedHandler(@NotNull String name, int size) {
        this.name = name;
        this.pool = new Thread[size];
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
        this.queue.offer(t);
    }

    private void threaded() {
        try {
            while (true) Objects.requireNonNull(this.queue.take()).handle(null);
        } catch (InterruptedException ignored) {
        }
    }
}

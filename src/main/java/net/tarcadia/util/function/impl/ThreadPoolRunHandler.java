package net.tarcadia.util.function.impl;

import net.tarcadia.util.function.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolRunHandler<T extends Runnable> implements Handler<T> {

    private final String name;
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Thread[] pool;

    private boolean interrupted = false;

    public ThreadPoolRunHandler(int size) {
        this.name = "";
        this.pool = new Thread[size];
        for (int i = 0; i < this.pool.length; i++) {
            this.pool[i] = new Thread(this::run);
            this.pool[i].start();
        }
    }

    public ThreadPoolRunHandler(@NotNull String name, int size) {
        this.name = name;
        this.pool = new Thread[size];
        for (int i = 0; i < this.pool.length; i++) {
            this.pool[i] = new Thread(this::run);
            this.pool[i].start();
        }
    }

    public void interrupt() {
        this.interrupted = true;
        for (final Thread thread : this.pool) {
            if (thread != null) thread.interrupt();
        }
    }

    public void clear() {
        this.queue.clear();
    }

    @Override
    public void handle(T t) {
        if (t != null) {
            this.queue.offer(t);
        }
    }

    private void run() {
        try {
            while (!this.interrupted) Objects.requireNonNull(this.queue.take()).run();
        } catch (InterruptedException ignored) {
        }
    }
}

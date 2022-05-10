package net.tarcadia.util.function.impl;

import net.tarcadia.util.function.Handler;
import net.tarcadia.util.function.Provider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolHandleHandler<T> implements Handler<T> {

    private final String name;
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Thread[] pool;
    private final Provider<T, Handler<T>> provider;

    private boolean interrupted = false;

    public ThreadPoolHandleHandler(int size, @NotNull Provider<T, Handler<T>> provider) {
        this.name = "";
        this.pool = new Thread[size];
        this.provider = provider;
        for (int i = 0; i < this.pool.length; i++) {
            this.pool[i] = new Thread(this::run);
            this.pool[i].start();
        }
    }

    public ThreadPoolHandleHandler(@NotNull String name, int size, @NotNull Provider<T, Handler<T>> provider) {
        this.name = name;
        this.pool = new Thread[size];
        this.provider = provider;
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

    public boolean isInterrupted() {
        return this.interrupted;
    }

    public void clear() {
        this.queue.clear();
    }

    @Override
    public void handle(T t) {
        Handler<T> handler = this.provider.provide(t);
        if (handler != null) {
            Runnable run = () -> handler.handle(t);
            this.queue.offer(run);
        }
    }

    private void run() {
        while (!this.interrupted) {
            try {
                this.queue.take().run();
            } catch (InterruptedException ignored) {
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}

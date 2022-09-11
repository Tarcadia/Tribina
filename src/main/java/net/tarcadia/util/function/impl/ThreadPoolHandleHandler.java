package net.tarcadia.util.function.impl;

import net.tarcadia.util.function.Handler;
import net.tarcadia.util.function.Provider;
import org.jetbrains.annotations.NotNull;

public class ThreadPoolHandleHandler<T> implements Handler<T> {

    private final ThreadPoolRunHandler<Runnable> pool;
    private final Provider<T, Handler<T>> provider;

    public ThreadPoolHandleHandler(int size, @NotNull Provider<T, Handler<T>> provider) {
        this.pool = new ThreadPoolRunHandler<>(size);
        this.provider = provider;
    }

    public ThreadPoolHandleHandler(@NotNull String name, int size, @NotNull Provider<T, Handler<T>> provider) {
        this.pool = new ThreadPoolRunHandler<>(name, size);
        this.provider = provider;
    }

    @Override
    public void handle(T t) {
        Handler<T> handler = this.provider.provide(t);
        if (handler != null) {
            this.pool.handle(() -> handler.handle(t));
        }
    }

}

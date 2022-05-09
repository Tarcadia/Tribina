package net.tarcadia.util.function.impl;

import net.tarcadia.util.function.Classifier;
import net.tarcadia.util.function.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class DispatchHandler<T> implements Handler<T> {

    private final String name;
    private final Collection<ClassifiedHandler<T>> handlers = new HashSet<>();

    public DispatchHandler() {
        this.name = "";
    }

    public DispatchHandler(@NotNull String name) {
        this.name = name;
    }

    public synchronized DispatchHandler<T> then(Classifier<T> classifier, DispatchHandler<T> handler) {
        this.handlers.add(new ClassifiedHandler<>(classifier, handler));
        return handler;
    }

    public synchronized Handler<T> fin(Classifier<T> classifier, Handler<T> handler) {
        this.handlers.add(new ClassifiedHandler<>(classifier, handler));
        return handler;
    }

    @Override
    public void handle(T t) {
        for (ClassifiedHandler<T> handler : handlers) {
            if (handler.classify(t)) handler.handle(t);
        }
    }
}

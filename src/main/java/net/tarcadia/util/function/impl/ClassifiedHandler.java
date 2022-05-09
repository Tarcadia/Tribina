package net.tarcadia.util.function.impl;

import net.tarcadia.util.function.Classifier;
import net.tarcadia.util.function.Handler;

public class ClassifiedHandler<T> implements Classifier<T>, Handler<T> {

    public static class AlwaysHandler<T> extends ClassifiedHandler<T> {
        public AlwaysHandler(Handler<T> handler) {
            super(Classifier.ALWAYS, handler);
        }
    }

    public static class NeverHandler<T> extends ClassifiedHandler<T> {
        public NeverHandler(Handler<T> handler) {
            super(Classifier.NEVER, handler);
        }
    }

    private final Classifier<T> classifier;
    private final Handler<T> handler;

    public ClassifiedHandler(Classifier<T> classifier, Handler<T> handler) {
        this.classifier = classifier;
        this.handler = handler;
    }

    @Override
    public boolean classify(T t) {
        return this.classifier.classify(t);
    }

    @Override
    public void handle(T t) {
        this.handler.handle(t);
    }
}

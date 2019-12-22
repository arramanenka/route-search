package com.romanenko.routefinder.graph;

import com.romanenko.routefinder.graph.model.Connection;

public interface MutableGraph<T> extends Graph<T> {

    default void add(T ownerInstance, T connectedInstance, int weight) {
        add(ownerInstance, new Connection<>(connectedInstance, weight));
    }

    void add(T ownerInstance, Connection<T> connection);

    void add(Graph<T> graph);

    void remove(T node);
}

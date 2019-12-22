package com.romanenko.routefinder.graph;

import com.romanenko.routefinder.graph.model.Connection;

public interface MutableGraph<T> extends Graph<T> {

    void add(T node, Connection<T> connection);

    void add(T node, Iterable<Connection<T>> connections);

    void add(Graph<T> graph);

    void remove(T node);
}

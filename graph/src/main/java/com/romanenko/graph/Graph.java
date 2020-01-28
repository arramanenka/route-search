package com.romanenko.graph;

import com.romanenko.graph.model.Connection;

import java.util.List;
import java.util.function.BiConsumer;

public interface Graph<T> {
    List<Connection<T>> getConnectionsForNode(T start);

    boolean contains(T node);

    void iterate(BiConsumer<T, Connection<T>> consumer);

    boolean storesSortedConnections();
}

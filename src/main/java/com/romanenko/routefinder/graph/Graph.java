package com.romanenko.routefinder.graph;

import com.romanenko.routefinder.graph.model.Connection;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public interface Graph<T> {
    @Nullable
    List<Connection<T>> getConnectionsForNode(T start);

    boolean contains(T node);

    void iterate(BiConsumer<T, Connection<T>> consumer);

    boolean storesSortedConnections();
}

package com.romanenko.graph.algo;

import com.romanenko.graph.Graph;
import com.romanenko.graph.model.Edge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class GraphAlgorithms {

    public static <T> Collection<T> bfs(Graph<T> source, T start, int maxWeight) {
        Set<T> result = new HashSet<>();
        bfs(source, start, maxWeight, graphConnection ->
                result.add(graphConnection.getConnection().getConnectedInstance())
        );
        return result;
    }

    public static <T> void bfs(Graph<T> source, T start, int maxWeight, Consumer<Edge<T>> action) {
        new ReachableNodesExtractor<>(source, start, maxWeight, action).doMagic();
    }
}

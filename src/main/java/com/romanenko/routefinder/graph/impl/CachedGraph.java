package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.Edge;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ToString
public class CachedGraph<T> implements Graph<T> {

    final Map<T, Graph<T>> cache = new ConcurrentHashMap<>();
    private final Graph<T> originalGraph;
    @Setter
    private int maxCachedWeight = Integer.MAX_VALUE;

    public CachedGraph(Graph<T> originalGraph) {
        this.originalGraph = originalGraph;
    }

    @Override
    public boolean contains(T node) {
        return originalGraph.contains(node);
    }

    @Override
    public Graph<T> getOptimalGraph(T start, int maxWeight) {
        return getGraphForSearch(start, maxWeight).getOptimalGraph(start, maxWeight);
    }

    @Override
    public Collection<T> getReachableNodes(T start, int maxWeight) {
        return getGraphForSearch(start, maxWeight).getReachableNodes(start, maxWeight);
    }

    @Override
    public void forEachReachableNode(T start, int maxWeight, Consumer<Edge<T>> action) {
        getGraphForSearch(start, maxWeight).forEachReachableNode(start, maxWeight, action);
    }

    // TODO: consider caching less, since now it is not memory efficient, although faster.
    //  As of right now we technically repeat ourselves by caching from every starting node.
    //  however, it is not memory efficient if we consider that some nodes have bigger "centrality"
    //  in case we have nodes connected in following way(in square brackets are weights of graph connections):
    //    5---[10]---1---[1]---2---[1]---3
    //  7-[]-6-[9]_/            \_[1]---4
    //  why should we cache 1 node, when technically we could get all nodes around < than desired weight and then call
    //  optimal graph from cache for those nodes.
    private Graph<T> getGraphForSearch(T start, int maxWeight) {
        if (maxWeight > maxCachedWeight) {
            return originalGraph;
        }
        return cache.computeIfAbsent(start, node -> originalGraph.getOptimalGraph(node, maxCachedWeight));
    }

    @Override
    public void iterate(BiConsumer<T, Connection<T>> consumer) {
        originalGraph.iterate(consumer);
    }
}

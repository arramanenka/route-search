package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.MutableGraph;
import com.romanenko.routefinder.graph.model.Connection;
import lombok.ToString;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Thread safe implementation of {@link MutableGraph} backed by {@link UnbalancedGraph}
 *
 * @param <T>
 */
@ToString
public class CachedGraph<T> implements MutableGraph<T> {

    final Map<T, Graph<T>> cache = new HashMap<>();
    private final MutableGraph<T> originalGraph = new UnbalancedGraph<>();

    @Override
    public void add(T ownerInstance, Connection<T> connection) {
        synchronized (originalGraph) {
            originalGraph.add(ownerInstance, connection);
            cache.clear();
        }
    }

    @Override
    public void add(Graph<T> graph) {
        synchronized (originalGraph) {
            originalGraph.add(graph);
            cache.clear();
        }
    }

    @Override
    public void remove(T node) {
        synchronized (originalGraph) {
            originalGraph.remove(node);
            cache.entrySet().removeIf(tGraphEntry -> tGraphEntry.getValue().contains(node));
        }
    }

    @Override
    public boolean contains(T node) {
        return originalGraph.contains(node);
    }

    @Override
    public Graph<T> getOptimalGraph(T start, int maxWeight) {
        return getCachedGraph(start).getOptimalGraph(start, maxWeight);
    }

    @Override
    public Collection<T> getReachableNodes(T start, int maxWeight) {
        return getCachedGraph(start).getReachableNodes(start, maxWeight);
    }

    private Graph<T> getCachedGraph(T start) {
        return cache.computeIfAbsent(start, node -> {
            synchronized (originalGraph) {
                Graph<T> graph = cache.get(node);
                if (graph == null) {
                    return originalGraph.getOptimalGraph(node, Integer.MAX_VALUE);
                }
                return graph;
            }
        });
    }

    @Override
    public void iterate(BiConsumer<T, Connection<T>> consumer) {
        synchronized (originalGraph) {
            originalGraph.iterate(consumer);
        }
    }
}

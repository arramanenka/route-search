package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.Edge;
import lombok.NonNull;
import lombok.ToString;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ToString
class OptimizedGraph<T> implements Graph<T> {
    private final T start;
    private final LinkedList<Edge<T>> edges;
    private final int maxWeight;

    OptimizedGraph(@NonNull T start, @NonNull LinkedList<Edge<T>> edges, boolean presorted) {
        this.start = start;
        this.edges = edges;
        if (!presorted) {
            this.edges.sort(Edge::compareTo);
        }
        if (edges.size() > 0) {
            maxWeight = edges.getLast().getOverallWeight();
        } else {
            maxWeight = 0;
        }
    }

    @Override
    public Graph<T> getOptimalGraph(T start, int maxWeight) {
        if (this.maxWeight <= maxWeight && this.start.equals(start)) {
            return this;
        }
        LinkedList<Edge<T>> resultList = new LinkedList<>();
        forEachReachableNode(start, maxWeight, resultList::add);
        return new OptimizedGraph<>(start, resultList, true);
    }

    @Override
    public Collection<T> getReachableNodes(T start, int maxWeight) {
        LinkedList<T> resultList = new LinkedList<>();
        forEachReachableNode(start, maxWeight, graphConnection ->
                resultList.add(graphConnection.getConnection().getConnectedInstance()));
        return resultList;
    }

    @Override
    public void forEachReachableNode(T start, int maxWeight, Consumer<Edge<T>> action) {
        if (this.start.equals(start) && maxWeight > 0) {
            for (Edge<T> edge : edges) {
                if (edge.getOverallWeight() > maxWeight) {
                    break;
                }
                action.accept(edge);
            }
        }
    }

    @Override
    public boolean contains(T node) {
        if (start.equals(node)) {
            return true;
        }
        for (Edge<T> edge : edges) {
            if (edge.getConnection().getConnectedInstance().equals(node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void iterate(BiConsumer<T, Connection<T>> consumer) {
        for (Edge<T> edge : edges) {
            consumer.accept(edge.getOwner(), edge.getConnection());
        }
    }
}

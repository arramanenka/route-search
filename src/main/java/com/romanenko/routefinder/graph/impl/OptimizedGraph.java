package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.GraphConnection;
import lombok.NonNull;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ToString
class OptimizedGraph<T> implements Graph<T> {
    private final T start;
    private final LinkedList<GraphConnection<T>> graphConnections;
    private final int maxWeight;

    OptimizedGraph(@NonNull T start, @NonNull LinkedList<GraphConnection<T>> graphConnections, boolean presorted) {
        this.start = start;
        this.graphConnections = graphConnections;
        if (!presorted) {
            this.graphConnections.sort(GraphConnection::compareTo);
        }
        if (graphConnections.size() > 0) {
            maxWeight = graphConnections.getLast().getOverallWeight();
        } else {
            maxWeight = 0;
        }
    }

    @Override
    public Graph<T> getOptimalGraph(T start, int maxWeight) {
        if (this.maxWeight <= maxWeight && this.start.equals(start)) {
            return this;
        }
        LinkedList<GraphConnection<T>> resultList = new LinkedList<>();
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
    public void forEachReachableNode(T start, int maxWeight, Consumer<GraphConnection<T>> action) {
        if (this.start.equals(start) && maxWeight > 0) {
            for (GraphConnection<T> graphConnection : graphConnections) {
                if (graphConnection.getOverallWeight() > maxWeight) {
                    break;
                }
                action.accept(graphConnection);
            }
        }
    }

    @Override
    public boolean contains(T node) {
        if (start.equals(node)) {
            return true;
        }
        for (GraphConnection<T> graphConnection : graphConnections) {
            if (graphConnection.getConnection().getConnectedInstance().equals(node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void iterate(BiConsumer<T, Connection<T>> consumer) {
        for (GraphConnection<T> graphConnection : graphConnections) {
            consumer.accept(graphConnection.getOwner(), graphConnection.getConnection());
        }
    }
}

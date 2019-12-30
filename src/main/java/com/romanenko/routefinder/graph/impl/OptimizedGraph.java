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
        if (!this.start.equals(start) || maxWeight <= 0) {
            return new OptimizedGraph<>(start, new LinkedList<>(), true);
        }
        if (this.maxWeight <= maxWeight) {
            return this;
        }
        LinkedList<GraphConnection<T>> resultList = new LinkedList<>();
        for (GraphConnection<T> graphConnection : this.graphConnections) {
            if (graphConnection.getOverallWeight() > maxWeight) {
                break;
            }
            resultList.add(graphConnection);
        }
        return new OptimizedGraph<>(start, resultList, true);
    }

    @Override
    public Collection<T> getReachableNodes(T start, int maxWeight) {
        if (this.start.equals(start)) {
            LinkedList<T> resultList = new LinkedList<>();
            for (GraphConnection<T> graphConnection : this.graphConnections) {
                if (graphConnection.getOverallWeight() > maxWeight) {
                    break;
                }
                resultList.add(graphConnection.getConnection().getConnectedInstance());
            }
            return resultList;
        }
        return Collections.emptyList();
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

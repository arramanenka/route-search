package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.MutableGraph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.GraphConnection;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Basic graph implementation, not threadsafe
 *
 * @param <T> type of node instances
 */
@ToString
@EqualsAndHashCode
public class UnbalancedGraph<T> implements MutableGraph<T> {

    final MultiValueMap<T, Connection<T>> nodeListMap = new LinkedMultiValueMap<>();

    @Override
    public Set<T> getReachableNodes(T start, int maxWeight) {
        Set<T> result = new HashSet<>();
        getGraphConnections(start, maxWeight, (owner, connection) -> result.add(connection.getInstance()));
        return result;
    }

    @Override
    public Graph<T> getOptimalGraph(T start, int maxWeight) {
        UnbalancedGraph<T> resultGraph = new UnbalancedGraph<>();

        getGraphConnections(start, maxWeight, resultGraph::add);

        return resultGraph;
    }

    private void getGraphConnections(T start, int maxWeight, BiConsumer<T, Connection<T>> onNewGraphFound) {
        List<Connection<T>> startConnections = nodeListMap.get(start);
        if (startConnections == null) {
            return;
        }

        Set<GraphConnection<T>> resultSet = new HashSet<>();
        PriorityQueue<GraphConnection<T>> priorityQueue = new PriorityQueue<>();

        for (Connection<T> connection : startConnections) {
            if (connection.getWeight() <= maxWeight) {
                priorityQueue.add(new GraphConnection<>(connection, start, connection.getWeight()));
            }
        }
        while (!priorityQueue.isEmpty()) {
            GraphConnection<T> graphConnection = priorityQueue.poll();
            // it is possible if we f.e. already found a less weighted pass to this indirectConnection
            if (resultSet.contains(graphConnection)) {
                continue;
            } else {
                resultSet.add(graphConnection);
                onNewGraphFound.accept(graphConnection.getOwner(), graphConnection.getConnection());
            }
            Connection<T> indirectConnection = graphConnection.getConnection();
            T indirectConnectionInstance = indirectConnection.getInstance();
            int graphWeight = graphConnection.getOverallWeight();

            for (Connection<T> connection : nodeListMap.get(indirectConnectionInstance)) {
                if (connection.getInstance().equals(start) || resultSet.contains(new GraphConnection<>(connection))) {
                    continue;
                }
                int overallWeight = graphWeight + connection.getWeight();
                if (overallWeight <= maxWeight) {
                    GraphConnection<T> g = new GraphConnection<>(connection, indirectConnectionInstance, overallWeight);
                    priorityQueue.offer(g);
                }
            }
        }
    }

    @Override
    public void add(T node, Connection<T> connection) {
        nodeListMap.add(node, connection);
        nodeListMap.computeIfAbsent(connection.getInstance(), e -> new LinkedList<>())
                .add(new Connection<>(node, connection.getWeight()));
    }

    @Override
    public void add(T node, Iterable<Connection<T>> connections) {
        for (Connection<T> connection : connections) {
            add(node, connection);
        }
    }

    @Override
    public void add(Graph<T> graph) {
        graph.iterate(this::add);
    }

    @Override
    public void remove(T node) {
        nodeListMap.remove(node);
        Connection<T> connection = new Connection<>(node, 0);
        nodeListMap.values().forEach(e -> e.remove(connection));
    }

    @Override
    public void iterate(BiConsumer<T, List<Connection<T>>> consumer) {
        for (Map.Entry<T, List<Connection<T>>> tListEntry : nodeListMap.entrySet()) {
            consumer.accept(tListEntry.getKey(), tListEntry.getValue());
        }
    }
}
package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.MutableGraph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.GraphConnection;
import lombok.ToString;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Basic graph implementation, not threadsafe
 * Recursive connections (instance having connection with itself) will be ignored
 *
 * @param <T> type of node instances
 */
@ToString
public class UnbalancedGraph<T> implements MutableGraph<T> {

    final MultiValueMap<T, Connection<T>> nodeListMap = new LinkedMultiValueMap<>();

    @Override
    public Set<T> getReachableNodes(T start, int maxWeight) {
        Set<T> result = new HashSet<>();
        getGraphConnections(start, maxWeight, (graphConnection) -> result.add(graphConnection.getConnection().getConnectedInstance()));
        return result;
    }

    @Override
    public Graph<T> getOptimalGraph(T start, int maxWeight) {
        LinkedList<GraphConnection<T>> resultList = new LinkedList<>();
        if (maxWeight > 0) {
            getGraphConnections(start, maxWeight, resultList::add);
        }
        // due to the nature of algorithm, we find optimal graph by going through least weighted connections,
        // thus list is presorted
        return new OptimizedGraph<>(start, resultList, true);
    }

    // todo: expose consumer. This would be hella useful for further moving to reactive controller
    private void getGraphConnections(T start, int maxWeight, Consumer<GraphConnection<T>> onNewGraphFound) {
        Set<GraphConnection<T>> resultSet = new HashSet<>();

        List<Connection<T>> startConnections = nodeListMap.get(start);
        if (startConnections == null) {
            return;
        }

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
                onNewGraphFound.accept(graphConnection);
            }
            Connection<T> indirectConnection = graphConnection.getConnection();
            T indirectConnectionInstance = indirectConnection.getConnectedInstance();
            int graphWeight = graphConnection.getOverallWeight();

            for (Connection<T> connection : nodeListMap.get(indirectConnectionInstance)) {
                if (connection.getConnectedInstance().equals(start) || resultSet.contains(new GraphConnection<>(connection))) {
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
    public void add(Graph<T> graph) {
        graph.iterate(this::add);
    }

    @Override
    public void add(T ownerInstance, Connection<T> connection) {
        // do not allow recursive connections
        if (connection.getConnectedInstance().equals(ownerInstance)) {
            return;
        }
        appendElementToMap(ownerInstance, connection);
        appendElementToMap(connection.getConnectedInstance(), new Connection<>(ownerInstance, connection.getWeight()));
    }

    //TODO elements in list of values in nodeListMap can be presorted by their weight,
    // this way once we hit element exceeding certain weight, we can break instead of checking every connection
    private void appendElementToMap(T node, Connection<T> connection) {
        List<Connection<T>> connections = nodeListMap.computeIfAbsent(node, e -> new LinkedList<>());
        int i = connections.indexOf(connection);
        if (i == -1) {
            connections.add(connection);
        } else {
            connections.set(i, connection);
        }
    }

    @Override
    public void remove(T node) {
        nodeListMap.remove(node);
        Connection<T> connection = new Connection<>(node, 0);
        Iterator<Map.Entry<T, List<Connection<T>>>> iterator = nodeListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<T, List<Connection<T>>> next = iterator.next();
            List<Connection<T>> connections = next.getValue();
            if (connections.isEmpty() || connections.remove(connection)) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean contains(T node) {
        return nodeListMap.containsKey(node);
    }

    @Override
    public void iterate(BiConsumer<T, Connection<T>> consumer) {
        for (Map.Entry<T, List<Connection<T>>> tListEntry : nodeListMap.entrySet()) {
            for (Connection<T> tConnection : tListEntry.getValue()) {
                consumer.accept(tListEntry.getKey(), tConnection);
            }
        }
    }
}
package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.MutableGraph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.GraphConnection;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.*;

/**
 * Basic graph implementation, not threadsafe
 * Recursive connections (instance having connection with itself) will be ignored
 *
 * @param <T> type of node instances
 */
@ToString
public class UnbalancedGraph<T> implements MutableGraph<T> {

    final MultiValueMap<T, Connection<T>> nodeListMap = new LinkedMultiValueMap<>();
    @Setter
    private boolean biDirectional = true;

    @Override
    public Set<T> getReachableNodes(T start, int maxWeight) {
        Set<T> result = new HashSet<>();
        forEachReachableNode(start, maxWeight, (graphConnection) -> result.add(graphConnection.getConnection().getConnectedInstance()));
        return result;
    }

    @Override
    public Graph<T> getOptimalGraph(T start, int maxWeight) {
        LinkedList<GraphConnection<T>> resultList = new LinkedList<>();
        if (maxWeight > 0) {
            forEachReachableNode(start, maxWeight, resultList::add);
        }
        // due to the nature of algorithm, we find optimal graph by going through least weighted connections,
        // thus list is presorted
        return new OptimizedGraph<>(start, resultList, true);
    }

    // todo: expose consumer. This would be hella useful for further moving to reactive controller.
    public void forEachReachableNode(T start, int maxWeight, Consumer<GraphConnection<T>> onNewGraphFound) {
        Set<GraphConnection<T>> resultSet = new HashSet<>();

        List<Connection<T>> startConnections = nodeListMap.get(start);
        if (CollectionUtils.isEmpty(startConnections)) {
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
            }
            resultSet.add(graphConnection);
            onNewGraphFound.accept(graphConnection);

            T indirectConnectionInstance = graphConnection.getConnection().getConnectedInstance();

            List<Connection<T>> connections = nodeListMap.get(indirectConnectionInstance);
            if (CollectionUtils.isEmpty(connections)) {
                continue;
            }
            for (Connection<T> connection : connections) {
                if (connection.getConnectedInstance().equals(start)
                        || resultSet.contains(new GraphConnection<>(connection))) {
                    continue;
                }
                int overallWeight = graphConnection.getOverallWeight() + connection.getWeight();
                // due to the fact that addition of connections is done in sorted manner, once we reach the element,
                // that has overallWeight bigger than desired, we can simply skip others, since they will have >= weight
                if (overallWeight > maxWeight) {
                    break;
                }
                priorityQueue.offer(new GraphConnection<>(connection, indirectConnectionInstance, overallWeight));
            }
        }
    }

    @Override
    public void add(T ownerInstance, Connection<T> connection) {
        // do not allow recursive connections
        if (connection.getConnectedInstance().equals(ownerInstance)) {
            return;
        }
        appendElementToMap(ownerInstance, connection);
        if (biDirectional) {
            appendElementToMap(connection.getConnectedInstance(), new Connection<>(ownerInstance, connection.getWeight()));
        }
    }

    private void appendElementToMap(T node, Connection<T> connection) {
        List<Connection<T>> connections = nodeListMap.computeIfAbsent(node, e -> new LinkedList<>());
        // we need to remove previous connection to the same node to avoid duplicates
        connections.remove(connection);

        ListIterator<Connection<T>> connectionListIterator = connections.listIterator();
        while (connectionListIterator.hasNext()) {
            Connection<T> storedConnection = connectionListIterator.next();
            if (storedConnection.compareTo(connection) >= 0) {
                connectionListIterator.previous();
                connectionListIterator.add(connection);
                return;
            }
        }
        connectionListIterator.add(connection);
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
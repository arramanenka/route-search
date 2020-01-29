package com.romanenko.graph.impl;

import com.romanenko.graph.MutableGraph;
import com.romanenko.graph.model.Connection;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Basic graph implementation, which stores connections sorting by weight. Not threadsafe
 * Recursive connections (instance having connection with itself) will be ignored.
 *
 * @param <T> type of node instances.
 */
@ToString
public class SortedGraph<T> implements MutableGraph<T> {

    final Map<T, List<Connection<T>>> nodeListMap = new HashMap<>();
    @Setter
    private boolean biDirectional = true;
    @Setter
    private BiFunction<T, Integer, Connection<T>> connectionProvider = Connection::new;

    @Override
    public boolean storesSortedConnections() {
        return true;
    }

    @Override
    public List<Connection<T>> getConnectionsForNode(T start) {
        return nodeListMap.get(start);
    }

    @Override
    public void add(T ownerInstance, T connectedInstance, int weight) {
        add(ownerInstance, connectionProvider.apply(connectedInstance, weight));
    }

    @Override
    public void add(T ownerInstance, Connection<T> connection) {
        // do not allow recursive connections
        if (connection.getConnectedInstance().equals(ownerInstance)) {
            return;
        }
        appendElementToMap(ownerInstance, connection);
        if (biDirectional) {
            appendElementToMap(
                    connection.getConnectedInstance(),
                    connectionProvider.apply(ownerInstance, connection.getWeight())
            );
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
        Connection<T> connection = connectionProvider.apply(node, 0);
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
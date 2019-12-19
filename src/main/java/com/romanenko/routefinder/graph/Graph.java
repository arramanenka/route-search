package com.romanenko.routefinder.graph;

import lombok.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Basic graph implementation, not synchronized
 *
 * @param <T> type of node instances
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Graph<T> {
    private static final Comparator<GraphConnection> graphConnectionComparator = Comparator.comparingInt(GraphConnection::getOverallWeight);
    @NonNull
    final Map<T, List<Connection<T>>> nodeListMap;
    private final boolean biDirectional;
    @Setter
    @NonNull
    private Supplier<List<Connection<T>>> collectionSupplier = ArrayList::new;

    /**
     * If you want raw result depicting only reachable nodes, this method is ok. Otherwise use {@link #getOptimalGraph}
     *
     * @param start     node from which we want to find all connected nodes (directly and indirectly)
     * @param maxWeight depicts max weight (sum of weights in indirect case)
     * @return list of connections, which contain destination node and weight it will take to get to it
     */
    public Set<T> getReachableNodes(T start, int maxWeight) {
        Set<T> result = new HashSet<>();
        getGraphConnections(start, maxWeight, e -> {
            result.add(e.connection.getInstance());
        });
        return result;
    }

    /**
     * @param start     node from which we want to find all connected nodes (directly and indirectly)
     * @param maxWeight depicts max weight (sum of weights in indirect case)
     * @return sub graph with optimal routes to each reachable node
     */
    public Graph<T> getOptimalGraph(T start, int maxWeight) {
        Graph<T> resultGraph = new Graph<>(new HashMap<>(), biDirectional);
        resultGraph.setCollectionSupplier(this.collectionSupplier);

        getGraphConnections(start, maxWeight, resultGraph::addGraphConnection);

        return resultGraph;
    }

    private void getGraphConnections(T start, int maxWeight, Consumer<GraphConnection<T>> onNewGraphFound) {
        Set<GraphConnection<T>> resultSet = new HashSet<>();

        PriorityQueue<GraphConnection<T>> priorityQueue = new PriorityQueue<>(graphConnectionComparator);
        Iterator<Connection<T>> startListIterator = nodeListMap.get(start).iterator();

        GraphConnection<T> startGraphConnection = null;
        GraphConnection<T> graphConnection;
        while (true) {
            for (; startGraphConnection == null && startListIterator.hasNext(); ) {
                Connection<T> next = startListIterator.next();
                if (next.getWeight() < maxWeight) {
                    startGraphConnection = new GraphConnection<>(next, start, next.getWeight());
                    break;
                }
            }
            if (priorityQueue.isEmpty()) {
                graphConnection = startGraphConnection;
                startGraphConnection = null;
            } else if (startGraphConnection == null || graphConnectionComparator.compare(startGraphConnection, priorityQueue.peek()) < 0) {
                graphConnection = priorityQueue.poll();
            } else {
                graphConnection = startGraphConnection;
                startGraphConnection = null;
            }
            if (graphConnection == null) {
                break;
            }
            // it is possible if we f.e. already found a less weighted pass to this indirectConnection
            if (resultSet.contains(graphConnection)) {
                continue;
            } else {
                resultSet.add(graphConnection);
                onNewGraphFound.accept(graphConnection);
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

    private void addGraphConnection(GraphConnection<T> graphConnection) {
        add(graphConnection.owner, graphConnection.connection);
    }

    public void add(T node) {
        nodeListMap.putIfAbsent(node, collectionSupplier.get());
    }

    public void add(T node, Connection<T> connection) {
        nodeListMap.putIfAbsent(node, collectionSupplier.get());
        nodeListMap.get(node).add(connection);
        if (biDirectional) {
            nodeListMap.computeIfAbsent(connection.getInstance(), e -> collectionSupplier.get())
                    .add(new Connection<>(node, connection.getWeight()));
        }
    }

    public void add(T node, Iterable<Connection<T>> connections) {
        nodeListMap.putIfAbsent(node, collectionSupplier.get());
        for (Connection<T> connection : connections) {
            nodeListMap.get(node).add(connection);
            if (biDirectional) {
                nodeListMap.computeIfAbsent(connection.getInstance(), e -> collectionSupplier.get())
                        .add(new Connection<>(node, connection.getWeight()));
            }
        }
    }

    public void add(Graph<T> graph) {
        for (Map.Entry<T, List<Connection<T>>> graphConnections : graph.nodeListMap.entrySet()) {
            add(graphConnections.getKey(), graphConnections.getValue());
        }
    }

    public void remove(T node) {
        nodeListMap.remove(node);
        Connection<T> connection = new Connection<>(node);
        nodeListMap.values().forEach(e -> e.remove(connection));
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class GraphConnection<T> {
        private final Connection<T> connection;
        @EqualsAndHashCode.Exclude
        private T owner;
        @EqualsAndHashCode.Exclude
        private int overallWeight;
    }
}
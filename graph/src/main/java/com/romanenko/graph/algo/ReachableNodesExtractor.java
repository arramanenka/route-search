package com.romanenko.graph.algo;

import com.romanenko.graph.Graph;
import com.romanenko.graph.model.Connection;
import com.romanenko.graph.model.Edge;
import lombok.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

class ReachableNodesExtractor<T> {
    private final T start;
    private final Consumer<Edge<T>> action;
    private final Function<T, Collection<Connection<T>>> indirectConnectionProvider;
    private final Function<Integer, CycleControl> stopTakingIndirectValues;
    private Set<Edge<T>> resultSet = new HashSet<>();
    private PriorityQueue<Edge<T>> potentialConnections = new PriorityQueue<>();

    ReachableNodesExtractor(@NonNull Graph<T> source, T start, int maxWeight, Consumer<Edge<T>> action) {
        this.start = start;
        this.action = action;
        stopTakingIndirectValues = source.storesSortedConnections()
                ? overallWeight -> (overallWeight > maxWeight ? CycleControl.BREAK : CycleControl.DO_NOTHING)
                : overallWeight -> (overallWeight > maxWeight ? CycleControl.CONTINUE : CycleControl.DO_NOTHING);
        indirectConnectionProvider = source::getConnectionsForNode;
        findConnectionsForEdge(new Edge<>(new Connection<>(start, 0), start, 0));
    }

    void doMagic() {
        while (!potentialConnections.isEmpty()) {
            Edge<T> edge = potentialConnections.poll();
            // it is possible if we f.e. already found a less weighted pass to this indirectConnection
            if (resultSet.contains(edge)) {
                continue;
            }
            resultSet.add(edge);
            action.accept(edge);
            findConnectionsForEdge(edge);
        }
    }

    private void findConnectionsForEdge(Edge<T> edge) {
        T indirectConnectionInstance = edge.getConnection().getConnectedInstance();

        Collection<Connection<T>> connections = indirectConnectionProvider.apply(indirectConnectionInstance);
        if (connections == null || connections.isEmpty()) {
            return;
        }
        for (Connection<T> connection : connections) {
            Edge<T> potentialConnection = new Edge<>(connection);
            if (connection.getConnectedInstance().equals(start) || resultSet.contains(potentialConnection)) {
                continue;
            }
            int overallWeight = edge.getOverallWeight() + connection.getWeight();
            CycleControl cycleControl = stopTakingIndirectValues.apply(overallWeight);
            if (cycleControl == CycleControl.BREAK) {
                break;
            } else if (cycleControl == CycleControl.CONTINUE) {
                continue;
            }
            potentialConnection.setOwner(indirectConnectionInstance);
            potentialConnection.setOverallWeight(overallWeight);
            potentialConnections.offer(potentialConnection);
        }
    }

    enum CycleControl {
        CONTINUE, BREAK, DO_NOTHING
    }
}

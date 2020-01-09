package com.romanenko.routefinder.graph;

import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.GraphConnection;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Graph<T> {

    /**
     * @param start     node from which we want to find all connected nodes (directly and indirectly)
     * @param maxWeight depicts max weight (sum of weights in indirect case)
     * @return sub graph with optimal routes to each reachable node
     */
    Graph<T> getOptimalGraph(T start, int maxWeight);

    /**
     * If you want raw result depicting only reachable nodes, this method is what you crave.
     * If you want to further find path to that nodes, use: {@link #getOptimalGraph}
     *
     * @param start     node from which we want to find all connected nodes (directly and indirectly)
     * @param maxWeight depicts max weight (sum of weights in indirect case)
     * @return list of connections (empty if graph does not contain node/ graph does not have connections,
     * weighted less than maxWeight), which contain destination node and weight it will take to get to it
     */
    Collection<T> getReachableNodes(T start, int maxWeight);

    /**
     * Find all reachable nodes and do certain action for those.
     * If you want to further find path to that nodes, use: {@link #getOptimalGraph}
     *
     * @param start     node from which we want to find all connected nodes (directly and indirectly)
     * @param maxWeight depicts max weight (sum of weights in indirect case)
     * @param action    what should be done.
     */
    void forEachReachableNode(T start, int maxWeight, Consumer<GraphConnection<T>> action);

    boolean contains(T node);

    void iterate(BiConsumer<T, Connection<T>> consumer);
}

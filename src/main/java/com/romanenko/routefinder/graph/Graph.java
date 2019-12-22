package com.romanenko.routefinder.graph;

import com.romanenko.routefinder.graph.model.Connection;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public interface Graph<T> {

    /**
     * @param start     node from which we want to find all connected nodes (directly and indirectly)
     * @param maxWeight depicts max weight (sum of weights in indirect case)
     * @return sub graph with optimal routes to each reachable node
     */
    Graph<T> getOptimalGraph(T start, int maxWeight);

    /**
     * If you want raw result depicting only reachable nodes, this method is what you crave.
     * If you want to further find path to that nodes use {@link #getOptimalGraph}
     *
     * @param start     node from which we want to find all connected nodes (directly and indirectly)
     * @param maxWeight depicts max weight (sum of weights in indirect case)
     * @return list of connections, which contain destination node and weight it will take to get to it
     */
    Set<T> getReachableNodes(T start, int maxWeight);

    void iterate(BiConsumer<T, List<Connection<T>>> consumer);
}

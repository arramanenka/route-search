package com.romanenko.graph;

import com.romanenko.graph.model.Connection;

public interface MutableGraph<T> extends Graph<T> {

    /**
     * Copy/Replace connections from given graph to this
     *
     * @param graph non null source graph
     */
    default void add(Graph<T> graph) {
        graph.iterate(this::add);
    }

    /**
     * Add/alter connection between two nodes.
     *
     * @param ownerInstance     node from which goes connection to connectedInstance
     * @param connectedInstance node to which goes connection
     * @param weight            connection's weight.
     */
    default void add(T ownerInstance, T connectedInstance, int weight) {
        add(ownerInstance, new Connection<>(connectedInstance, weight));
    }

    /**
     * Add/alter connection between two nodes
     *
     * @param ownerInstance node from which goes connection
     * @param connection    details about connectedInstance, and it's weight
     */
    void add(T ownerInstance, Connection<T> connection);

    /**
     * Remove all connections to the node and node itself from graph
     *
     * @param node node to be removed
     */
    void remove(T node);
}

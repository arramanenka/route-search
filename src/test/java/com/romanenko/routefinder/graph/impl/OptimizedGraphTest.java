package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.Edge;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static com.romanenko.routefinder.graph.impl.GraphTestUtil.assertResultForParametersOutOfGraphIsEmpty;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptimizedGraphTest {
    @Test
    void testGraphReturnEmptyResultsForIncorrectQueries() {
        LinkedList<Edge<Integer>> edges = new LinkedList<>();
        edges.add(new Edge<>(new Connection<>(2, 3), 1, 3));
        OptimizedGraph<Integer> optimizedGraph = new OptimizedGraph<>(1, edges, true);

        assertResultForParametersOutOfGraphIsEmpty(optimizedGraph);
    }

    @Test
    void testGraphContainsForBothKeysAndValues() {
        LinkedList<Edge<Integer>> connections = new LinkedList<>();
        connections.add(new Edge<>(new Connection<>(3, 3), 1, 3));
        OptimizedGraph<Integer> optimizedGraph = new OptimizedGraph<>(1, connections, false);

        assertTrue(optimizedGraph.contains(1));
        assertTrue(optimizedGraph.contains(3));
        assertFalse(optimizedGraph.contains(4));
    }
}

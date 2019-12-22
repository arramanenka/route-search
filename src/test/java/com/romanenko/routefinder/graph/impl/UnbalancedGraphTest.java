package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.MutableGraph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnbalancedGraphTest {

    @Test
    void testRetrievalOfReachableNodes() {
        //prep
        MutableGraph<Integer> graph = new UnbalancedGraph<>();
        graph.add(1, new Connection<>(2, 1));
        graph.add(1, new Connection<>(3, 10));
        graph.add(1, new Connection<>(4, 100));
        graph.add(2, new Connection<>(3, 1));
        Set<Integer> expectedResult = new HashSet<>();
        expectedResult.add(2);
        expectedResult.add(3);
        //act
        Set<Integer> reachableNodes = graph.getReachableNodes(1, 10);
        //assert
        assertEquals(expectedResult, reachableNodes);
    }

    @Test
    void testOptimalPathGraphCreation() {
        //prep
        MutableGraph<Integer> optimalGraph = new UnbalancedGraph<>();

        optimalGraph.add(1, new Connection<>(2, 1));
        optimalGraph.add(2, new Connection<>(3, 1));
        optimalGraph.add(3, new Connection<>(4, 1));

        MutableGraph<Integer> graph = new UnbalancedGraph<>();
        graph.add(optimalGraph);
        graph.add(1, new Connection<>(3, 3));
        graph.add(1, new Connection<>(4, 3));
        graph.add(2, new Connection<>(4, 3));
        // action
        Graph<Integer> subGraph = graph.getOptimalGraph(1, 10);
        //assert
        assertEquals(optimalGraph, subGraph);
    }
}

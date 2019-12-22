package com.romanenko.routefinder.graph;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GraphTest {

    @Test
    void testAddingBiDirectional() {
        // prep
        Graph<Integer> graph = prepareTestGraph(true);
        // assert
        assertEquals(3, graph.nodeListMap.keySet().size());
        checkConnections(graph, 1, 1);
        checkConnections(graph, 2, 1);
        checkConnections(graph, 3, 0);
    }

    @Test
    void testAddingNonBiDirectional() {
        // prep
        Graph<Integer> graph = prepareTestGraph(false);
        // assert
        assertEquals(3, graph.nodeListMap.keySet().size());
        checkConnections(graph, 1, 0);
        checkConnections(graph, 2, 1);
        checkConnections(graph, 3, 0);
    }

    @Test
    void testRemovingConnectedFromBiDirectional() {
        // prep
        Graph<Integer> graph = prepareTestGraph(true);
        // act
        graph.remove(1);
        // assert
        checkConnections(graph, 2, 0);
        checkConnections(graph, 3, 0);
    }

    @Test
    void testRemovingConnectedFromNonBiDirectional() {
        // prep
        Graph<Integer> graph = prepareTestGraph(false);
        // act
        graph.remove(1);
        // assert
        checkConnections(graph, 2, 0);
        checkConnections(graph, 3, 0);
    }

    @Test
    void testRemovingNotConnectedFromBiDirectional() {
        // prep
        Graph<Integer> graph = prepareTestGraph(true);
        // act
        graph.remove(3);
        // assert
        checkConnections(graph, 1, 1);
        checkConnections(graph, 2, 1);
    }

    @Test
    void testRemovingNotConnectedFromNonBiDirectional() {
        // prep
        Graph<Integer> graph = prepareTestGraph(false);
        // act
        graph.remove(3);
        // assert
        checkConnections(graph, 1, 0);
        checkConnections(graph, 2, 1);
    }

    @Test
    void testRetrievalOfReachableNodes() {
        //prep
        Graph<Integer> graph = new Graph<>(new HashMap<>(), true);
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
        Graph<Integer> optimalGraph = new Graph<>(new HashMap<>(), true);

        optimalGraph.add(1, new Connection<>(2, 1));
        optimalGraph.add(2, new Connection<>(3, 1));
        optimalGraph.add(3, new Connection<>(4, 1));

        Graph<Integer> graph = new Graph<>(new HashMap<>(), true);
        graph.add(optimalGraph);
        graph.add(1, new Connection<>(3, 3));
        graph.add(1, new Connection<>(4, 3));
        graph.add(2, new Connection<>(4, 3));
        // action
        Graph<Integer> subGraph = graph.getOptimalGraph(1, 10);
        //assert
        assertEquals(optimalGraph, subGraph);
    }

    private Graph<Integer> prepareTestGraph(boolean biDirectional) {
        Graph<Integer> graph = new Graph<>(new HashMap<>(), biDirectional);
        graph.add(1);
        graph.add(2, new Connection<>(1, 1));
        graph.add(3);
        return graph;
    }

    private void checkConnections(Graph<Integer> graph, int s, int i) {
        List<Connection<Integer>> pamPamConnections = graph.nodeListMap.get(s);
        assertNotNull(pamPamConnections);
        assertEquals(i, pamPamConnections.size());
    }
}

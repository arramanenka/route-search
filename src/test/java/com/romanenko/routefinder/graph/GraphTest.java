package com.romanenko.routefinder.graph;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GraphTest {
    private static final String firstNode = "1";
    private static final String secondNode = "2";
    private static final String thirdNode = "3";
    private static final String fourthNode = "4";

    @Test
    void testAddingBiDirectional() {
        // prep
        Graph<String> graph = prepareTestGraph(true);
        // assert
        assertEquals(3, graph.nodeListMap.keySet().size());
        checkConnections(graph, firstNode, 1);
        checkConnections(graph, secondNode, 1);
        checkConnections(graph, thirdNode, 0);
    }

    @Test
    void testAddingNonBiDirectional() {
        // prep
        Graph<String> graph = prepareTestGraph(false);
        // assert
        assertEquals(3, graph.nodeListMap.keySet().size());
        checkConnections(graph, firstNode, 0);
        checkConnections(graph, secondNode, 1);
        checkConnections(graph, thirdNode, 0);
    }

    @Test
    void testRemovingConnectedFromBiDirectional() {
        // prep
        Graph<String> graph = prepareTestGraph(true);
        // act
        graph.remove(firstNode);
        // assert
        checkConnections(graph, secondNode, 0);
        checkConnections(graph, thirdNode, 0);
    }

    @Test
    void testRemovingConnectedFromNonBiDirectional() {
        // prep
        Graph<String> graph = prepareTestGraph(false);
        // act
        graph.remove(firstNode);
        // assert
        checkConnections(graph, secondNode, 0);
        checkConnections(graph, thirdNode, 0);
    }

    @Test
    void testRemovingNotConnectedFromBiDirectional() {
        // prep
        Graph<String> graph = prepareTestGraph(true);
        // act
        graph.remove(thirdNode);
        // assert
        checkConnections(graph, firstNode, 1);
        checkConnections(graph, secondNode, 1);
    }

    @Test
    void testRemovingNotConnectedFromNonBiDirectional() {
        // prep
        Graph<String> graph = prepareTestGraph(false);
        // act
        graph.remove(thirdNode);
        // assert
        checkConnections(graph, firstNode, 0);
        checkConnections(graph, secondNode, 1);
    }

    @Test
    void testRetrievalOfReachableNodesBasic() {
        //prep
        Graph<String> graph = new Graph<>(new HashMap<>(), true);
        graph.add(firstNode, new Connection<>(secondNode, 1));
        graph.add(firstNode, new Connection<>(thirdNode, 10));
        graph.add(firstNode, new Connection<>(fourthNode, 100));
        graph.add(secondNode, new Connection<>(thirdNode, 1));
        Set<String> expectedResult = new HashSet<>();
        expectedResult.add(secondNode);
        expectedResult.add(thirdNode);
        //act
        Set<String> reachableNodes = graph.getReachableNodes(firstNode, 10);
        //assert
        assertEquals(expectedResult, reachableNodes);
    }

    @Test
    void testOptimalPathGraphCreation() {
        //prep
        Graph<String> optimalGraph = new Graph<>(new HashMap<>(), true);

        optimalGraph.add(firstNode, new Connection<>(secondNode, 1));
        optimalGraph.add(secondNode, new Connection<>(thirdNode, 1));
        optimalGraph.add(thirdNode, new Connection<>(fourthNode, 1));

        Graph<String> graph = new Graph<>(new HashMap<>(), true);
        graph.add(optimalGraph);
        graph.add(firstNode, new Connection<>(thirdNode, 3));
        graph.add(firstNode, new Connection<>(fourthNode, 3));
        graph.add(secondNode, new Connection<>(fourthNode, 3));
        // action
        Graph<String> subGraph = graph.getOptimalGraph(firstNode, 10);
        //assert
        assertEquals(optimalGraph, subGraph);
    }

    private Graph<String> prepareTestGraph(boolean biDirectional) {
        Graph<String> graph = new Graph<>(new HashMap<>(), biDirectional);
        graph.add(firstNode);
        graph.add(secondNode, new Connection<>(firstNode, 1));
        graph.add(thirdNode);
        return graph;
    }

    private void checkConnections(Graph<String> graph, String s, int i) {
        List<Connection<String>> pamPamConnections = graph.nodeListMap.get(s);
        assertNotNull(pamPamConnections);
        assertEquals(i, pamPamConnections.size());
    }
}

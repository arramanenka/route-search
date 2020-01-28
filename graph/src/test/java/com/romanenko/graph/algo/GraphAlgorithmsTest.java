package com.romanenko.graph.algo;

import com.romanenko.graph.impl.SortedGraph;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphAlgorithmsTest {
    @Test
    void testRetrievalOfReachableNodes() {
        //prep
        SortedGraph<Integer> graph = new SortedGraph<>();
        graph.add(1, 2, 1);
        graph.add(1, 3, 10);
        graph.add(1, 4, 100);
        graph.add(2, 3, 1);
        Set<Integer> expectedResult = new HashSet<>();
        expectedResult.add(2);
        expectedResult.add(3);
        //act
        Collection<Integer> reachableNodes = GraphAlgorithms.bfs(graph, 1, 10);
        //assert
        assertEquals(expectedResult, reachableNodes);
    }

    @Test
    void testGraphConnectionWeightDistributionNotAffectingAlgorithm() {
        SortedGraph<Integer> sortedGraph = new SortedGraph<>();
        sortedGraph.add(1, 2, 1);
        sortedGraph.add(1, 6, 100);
        sortedGraph.add(1, 7, 1);

        sortedGraph.add(2, 3, 500);
        sortedGraph.add(2, 4, 1);
        sortedGraph.add(2, 5, 600);

        Collection<Integer> reachableNodes = GraphAlgorithms.bfs(sortedGraph, 1, 2);

        assertEquals(3, reachableNodes.size());
        assertTrue(reachableNodes.contains(7));
        assertTrue(reachableNodes.contains(2));
        assertTrue(reachableNodes.contains(4));
    }

    @Test
    void testGraphNonSortedGraphSearch() {
        SortedGraph<Integer> sortedGraph = Mockito.spy(new SortedGraph<>());
        Mockito.when(sortedGraph.storesSortedConnections()).thenReturn(false);
        sortedGraph.add(1, 2, 1);
        sortedGraph.add(1, 6, 100);
        sortedGraph.add(1, 7, 1);

        sortedGraph.add(2, 3, 500);
        sortedGraph.add(2, 4, 1);
        sortedGraph.add(2, 5, 600);

        Collection<Integer> reachableNodes = GraphAlgorithms.bfs(sortedGraph, 1, 2);

        assertEquals(3, reachableNodes.size());
        assertTrue(reachableNodes.contains(7));
        assertTrue(reachableNodes.contains(2));
        assertTrue(reachableNodes.contains(4));
    }


    @Test
    void testNonBidirectional() {
        SortedGraph<Integer> nonBidiGraph = new SortedGraph<>();
        nonBidiGraph.setBiDirectional(false);
        nonBidiGraph.add(1, 2, 1);
        nonBidiGraph.add(2, 3, 1);
        nonBidiGraph.add(3, 4, 1);

        assertEquals(3, GraphAlgorithms.bfs(nonBidiGraph, 1, 3).size());
        assertEquals(2, GraphAlgorithms.bfs(nonBidiGraph, 2, Integer.MAX_VALUE).size());
        assertEquals(1, GraphAlgorithms.bfs(nonBidiGraph, 3, Integer.MAX_VALUE).size());
        assertEquals(0, GraphAlgorithms.bfs(nonBidiGraph, 4, Integer.MAX_VALUE).size());
    }
}
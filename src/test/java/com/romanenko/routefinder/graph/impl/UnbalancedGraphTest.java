package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.GraphConnection;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.romanenko.routefinder.graph.impl.GraphTestUtil.assertGraphEquals;
import static com.romanenko.routefinder.graph.impl.GraphTestUtil.assertResultForParametersOutOfGraphIsEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class UnbalancedGraphTest {

    @Test
    void testRetrievalOfReachableNodes() {
        //prep
        UnbalancedGraph<Integer> graph = new UnbalancedGraph<>();
        graph.add(1, 2, 1);
        graph.add(1, 3, 10);
        graph.add(1, 4, 100);
        graph.add(2, 3, 1);
        Set<Integer> expectedResult = new HashSet<>();
        expectedResult.add(2);
        expectedResult.add(3);
        //act
        Collection<Integer> reachableNodes = graph.getReachableNodes(1, 10);
        //assert
        assertEquals(expectedResult, reachableNodes);
    }

    @Test
    void testOptimalPathGraphCreation() {
        //prep
        LinkedList<GraphConnection<Integer>> result = new LinkedList<>();
        result.add(new GraphConnection<>(new Connection<>(2, 1), 1, 1));
        result.add(new GraphConnection<>(new Connection<>(3, 1), 2, 2));
        result.add(new GraphConnection<>(new Connection<>(4, 1), 3, 3));
        result.add(new GraphConnection<>(new Connection<>(5, 1), 3, 3));
        OptimizedGraph<Integer> optimizedGraph = new OptimizedGraph<>(1, result, false);

        UnbalancedGraph<Integer> graph = new UnbalancedGraph<>();
        graph.add(optimizedGraph);
        graph.add(1, 3, 3);
        graph.add(1, 4, 3);
        graph.add(2, 4, 3);
        // act
        Graph<Integer> subGraph = graph.getOptimalGraph(1, 10);
        //assert
        assertGraphEquals(optimizedGraph, subGraph);
    }

    @Test
    void testOptimalGraphSearchConsistency() {
        //prep
        UnbalancedGraph<Integer> fullGraph = new UnbalancedGraph<>();
        fullGraph.add(1, 2, 3);
        fullGraph.add(1, 3, 1);
        fullGraph.add(2, 3, 1);
        fullGraph.add(2, 4, 5);
        //act
        Graph<Integer> optimalGraph = fullGraph.getOptimalGraph(1, Integer.MAX_VALUE);
        //assert
        assertGraphEquals(optimalGraph, optimalGraph.getOptimalGraph(1, Integer.MAX_VALUE));
        assertGraphEquals(fullGraph.getOptimalGraph(1, 3), optimalGraph.getOptimalGraph(1, 3));
    }


    @Test
    void testBasicGraphOperations() {
        //prep
        UnbalancedGraph<Integer> unbalancedGraph = new UnbalancedGraph<>();
        Graph<Integer> graphToBeAdded = mock(Graph.class);
        Mockito.doAnswer(invocationOnMock -> {
            BiConsumer<Integer, Connection<Integer>> consumer = invocationOnMock.getArgument(0);
            consumer.accept(5, new Connection<>(6, 10));
            consumer.accept(5, new Connection<>(6, 10));
            consumer.accept(1, new Connection<>(2, 3));
            return null;
        }).when(graphToBeAdded).iterate(any());
        //act
        unbalancedGraph.add(graphToBeAdded);
        unbalancedGraph.remove(1);
        //assert
        assertEquals(2, unbalancedGraph.nodeListMap.size());
        assertEquals(unbalancedGraph.nodeListMap.get(5).size(), 1);
        assertEquals(unbalancedGraph.nodeListMap.get(6).size(), 1);
        assertEquals(unbalancedGraph.nodeListMap.get(5).get(0).getWeight(), 10);
        assertEquals(unbalancedGraph.nodeListMap.get(6).get(0).getWeight(), 10);
        assertFalse(unbalancedGraph.contains(1));
        assertTrue(unbalancedGraph.contains(5));
    }

    @Test
    void testGraphReturnEmptyResultsForIncorrectQueries() {
        UnbalancedGraph<Integer> unbalancedGraph = new UnbalancedGraph<>();
        unbalancedGraph.add(1, 2, 3);

        assertResultForParametersOutOfGraphIsEmpty(unbalancedGraph);
    }

    @Test
    void testGraphConnectionWeightDistributionNotAffectingAlgorithm() {
        UnbalancedGraph<Integer> unbalancedGraph = new UnbalancedGraph<>();
        unbalancedGraph.add(1, 2, 1);

        unbalancedGraph.add(2, 3, 500);
        unbalancedGraph.add(2, 4, 1);
        unbalancedGraph.add(2, 5, 600);


        Set<Integer> reachableNodes = unbalancedGraph.getReachableNodes(1, 2);

        assertEquals(2, reachableNodes.size());
        assertTrue(reachableNodes.contains(2));
        assertTrue(reachableNodes.contains(4));
    }

    @Test
    void testContinuousReachableNodesSearch() {
        //prep
        UnbalancedGraph<Integer> fullGraph = new UnbalancedGraph<>();
        fullGraph.add(1, 2, 3);
        fullGraph.add(1, 3, 1);
        fullGraph.add(2, 3, 1);
        fullGraph.add(2, 4, 5);
        //act
        Graph<Integer> optimalGraph = fullGraph.getOptimalGraph(1, Integer.MAX_VALUE);
        //assert
        assertThat(optimalGraph.getReachableNodes(1, Integer.MAX_VALUE))
                .containsExactlyInAnyOrderElementsOf(fullGraph.getReachableNodes(1, Integer.MAX_VALUE));
        assertThat(optimalGraph.getReachableNodes(1, 3))
                .containsExactlyInAnyOrderElementsOf(fullGraph.getReachableNodes(1, 3));
        assertThat(optimalGraph.getReachableNodes(66, 6))
                .containsExactlyInAnyOrderElementsOf(fullGraph.getReachableNodes(66, 6));
    }
}

package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.model.Connection;
import com.romanenko.routefinder.graph.model.GraphConnection;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class GraphTest {

    @Test
    void testEmptyCollectionReturnedForNodesOutsideOfGraph() {
        //prep
        UnbalancedGraph<Integer> unbalancedGraph = new UnbalancedGraph<>();
        //act
        Set<Integer> reachableNodes = unbalancedGraph.getReachableNodes(66, 6);
        unbalancedGraph.getOptimalGraph(66, 6).iterate((node, connection) -> {
            throw new AssertionError("Consumer should not be called: graph should be empty");
        });
        //assert
        assertEquals(0, reachableNodes.size());
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

    @Test
    void testGraphContainsForBothKeysAndValues() {
        LinkedList<GraphConnection<Integer>> connections = new LinkedList<>();
        connections.add(new GraphConnection<>(new Connection<>(3, 3), 1, 3));
        OptimizedGraph<Integer> optimizedGraph = new OptimizedGraph<>(1, connections, false);

        CachedGraph<Integer> cachedGraph = new CachedGraph<>();
        cachedGraph.add(optimizedGraph);

        assertTrue(optimizedGraph.contains(1));
        assertTrue(optimizedGraph.contains(3));
        assertFalse(optimizedGraph.contains(4));

        assertTrue(cachedGraph.contains(1));
        assertTrue(cachedGraph.contains(3));
        assertFalse(cachedGraph.contains(4));
    }

    @Test
    void testCachedGraphCaching() {
        // prep
        CachedGraph<Integer> cachedGraph = new CachedGraph<>();
        cachedGraph.add(1, 2, 3);
        cachedGraph.add(1, 5, 7);
        // act
        Graph<Integer> optimalGraph = cachedGraph.getOptimalGraph(1, 3);
        // assert
        assertEquals(1, cachedGraph.cache.size());
        assertGraphEquals(optimalGraph, cachedGraph.getOptimalGraph(1, 3));
        assertEquals(optimalGraph.getReachableNodes(1, 3), cachedGraph.getReachableNodes(1, 3));

        // act
        cachedGraph.remove(5);
        // assert
        assertEquals(0, cachedGraph.cache.size());
    }

    private <T> void assertGraphEquals(Graph<T> left, Graph<T> right) {
        MultiValueMap<T, Connection<T>> connections = new LinkedMultiValueMap<>();
        left.iterate(connections::add);
        right.iterate((node, rightGraphsConnection) -> {
            List<Connection<T>> nodeConnections = connections.get(node);
            if (CollectionUtils.isEmpty(nodeConnections)) {
                throw new AssertionError(String.format(
                        """
                        Could not find any rightGraphsConnection %s from node %s in left graph.
                        Inspected graphs:
                        %s
                        %s
                        """, rightGraphsConnection, node, left, right
                ));
            }
            int i = nodeConnections.indexOf(rightGraphsConnection);
            if (i == -1) {
                throw new AssertionError(String.format(
                        """
                        Left graph does not contain rightGraphsConnection %s from node %s. Inspected graphs:
                        %s
                        %s
                        """,
                        rightGraphsConnection, node, left, right
                ));
            }
            Connection<T> leftGraphsConnection = nodeConnections.get(i);
            if (leftGraphsConnection.getWeight() != rightGraphsConnection.getWeight()) {
                throw new AssertionError(String.format(
                        """
                        Left and right graph's connections %s and %s have different weights.
                        Inspected graphs:
                        %s
                        %s
                        """,
                        leftGraphsConnection, rightGraphsConnection, left, right
                ));
            }
            nodeConnections.remove(leftGraphsConnection);
            if (nodeConnections.isEmpty()) {
                connections.remove(node);
            }
        });
        if (!connections.isEmpty()) {
            throw new AssertionError(String.format(
                    """
                    Left graph contains elements which are not present in the right one:
                    %s
                    """, connections
            ));
        }
    }
}

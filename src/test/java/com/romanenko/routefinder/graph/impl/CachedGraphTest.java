package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import org.junit.jupiter.api.Test;

import static com.romanenko.routefinder.graph.impl.GraphTestUtil.assertGraphEquals;
import static com.romanenko.routefinder.graph.impl.GraphTestUtil.assertResultForParametersOutOfGraphIsEmpty;
import static org.junit.jupiter.api.Assertions.*;

class CachedGraphTest {

    @Test
    void testGraphReturnEmptyResultsForIncorrectQueries() {
        UnbalancedGraph<Integer> source = new UnbalancedGraph<>();
        source.add(1, 2, 3);

        CachedGraph<Integer> cachedGraph = new CachedGraph<>(source);

        assertResultForParametersOutOfGraphIsEmpty(cachedGraph);
        // Double checking cached graph implementation just in case there were some troubles after caching
        assertResultForParametersOutOfGraphIsEmpty(cachedGraph);
    }

    @Test
    void testGraphCorrectWorkingForQueriesExceedingMaxCachedWeight() {
        UnbalancedGraph<Integer> source = new UnbalancedGraph<>();
        source.add(1, 2, 3);
        source.add(2, 3, 4);
        source.add(3, 5, 6);

        CachedGraph<Integer> cachedGraph = new CachedGraph<>(source);
        cachedGraph.setMaxCachedWeight(0);

        assertGraphEquals(
                source.getOptimalGraph(1, 6),
                cachedGraph.getOptimalGraph(1, 6)
        );
        assertTrue(cachedGraph.cache.isEmpty());
        assertEquals(
                source.getReachableNodes(1, 6),
                cachedGraph.getReachableNodes(1, 6)
        );
        assertTrue(cachedGraph.cache.isEmpty());

    }

    @Test
    void testGraphContainsForBothKeysAndValues() {
        UnbalancedGraph<Integer> source = new UnbalancedGraph<>();
        source.add(1, 3, 4);
        CachedGraph<Integer> cachedGraph = new CachedGraph<>(source);

        assertTrue(cachedGraph.contains(1));
        assertTrue(cachedGraph.contains(3));
        assertFalse(cachedGraph.contains(4));
    }

    @Test
    void testGraphCaching() {
        // prep
        UnbalancedGraph<Integer> source = new UnbalancedGraph<>();
        source.add(1, 2, 3);
        source.add(1, 5, 7);
        CachedGraph<Integer> cachedGraph = new CachedGraph<>(source);
        // act
        Graph<Integer> optimalGraph = cachedGraph.getOptimalGraph(1, 3);
        // assert
        assertEquals(1, cachedGraph.cache.size());
        assertGraphEquals(optimalGraph, cachedGraph.getOptimalGraph(1, 3));
        assertEquals(optimalGraph.getReachableNodes(1, 3), cachedGraph.getReachableNodes(1, 3));
    }
}

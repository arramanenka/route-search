package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import org.junit.jupiter.api.Test;

import static com.romanenko.routefinder.graph.impl.GraphTestUtil.assertGraphEquals;
import static com.romanenko.routefinder.graph.impl.GraphTestUtil.assertResultForParametersOutOfGraphIsEmpty;
import static org.junit.jupiter.api.Assertions.*;

public class CachedGraphTest {

    @Test
    void testGraphReturnEmptyResultsForIncorrectQueries() {
        CachedGraph<Integer> cachedGraph = new CachedGraph<>();
        cachedGraph.add(1, 2, 3);

        assertResultForParametersOutOfGraphIsEmpty(cachedGraph);
        // Double checking cached graph implementation just in case there were some troubles after caching
        assertResultForParametersOutOfGraphIsEmpty(cachedGraph);
    }

    @Test
    void testGraphContainsForBothKeysAndValues() {
        CachedGraph<Integer> cachedGraph = new CachedGraph<>();
        cachedGraph.add(1, 3, 4);

        assertTrue(cachedGraph.contains(1));
        assertTrue(cachedGraph.contains(3));
        assertFalse(cachedGraph.contains(4));
    }

    @Test
    void testGraphCaching() {
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
}

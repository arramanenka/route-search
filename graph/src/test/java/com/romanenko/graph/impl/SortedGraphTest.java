package com.romanenko.graph.impl;

import com.romanenko.graph.Graph;
import com.romanenko.graph.model.Connection;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class SortedGraphTest {
    @Test
    void testBasicGraphOperations() {
        //prep
        SortedGraph<Integer> sortedGraph = new SortedGraph<>();
        Graph<Integer> graphToBeAdded = mock(Graph.class);
        Mockito.doAnswer(invocationOnMock -> {
            BiConsumer<Integer, Connection<Integer>> consumer = invocationOnMock.getArgument(0);
            consumer.accept(5, new Connection<>(6, 10));
            consumer.accept(5, new Connection<>(6, 10));
            consumer.accept(1, new Connection<>(2, 3));
            return null;
        }).when(graphToBeAdded).iterate(any());
        //act
        sortedGraph.add(graphToBeAdded);
        sortedGraph.remove(1);
        //assert
        assertEquals(2, sortedGraph.nodeListMap.size());
        assertEquals(sortedGraph.getConnectionsForNode(5).size(), 1);
        assertEquals(sortedGraph.getConnectionsForNode(6).size(), 1);
        assertEquals(sortedGraph.getConnectionsForNode(5).get(0).getWeight(), 10);
        assertEquals(sortedGraph.getConnectionsForNode(6).get(0).getWeight(), 10);
        assertFalse(sortedGraph.contains(1));
        assertTrue(sortedGraph.contains(5));
    }

    @Test
    void testRecursiveGraphConnectionNotAdded() {
        SortedGraph<Integer> intGraph = new SortedGraph<>();

        intGraph.add(1, 1, 10);

        assertFalse(intGraph.nodeListMap.containsKey(1));
    }
}

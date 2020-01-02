package com.romanenko.routefinder.graph.impl;

import com.romanenko.routefinder.graph.Graph;
import com.romanenko.routefinder.graph.model.Connection;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphTestUtil {

    private GraphTestUtil() {
    }

    public static void assertResultForParametersOutOfGraphIsEmpty(Graph<Integer> graph) {
        assertTrue(graph.getReachableNodes(1, 0).isEmpty());
        assertTrue(graph.getReachableNodes(0, 10).isEmpty());

        graph.getOptimalGraph(0, 10).iterate((a, b) -> {
            throw new AssertionError("Graph should be empty");
        });
    }

    public static <T> void assertGraphEquals(Graph<T> left, Graph<T> right) {
        MultiValueMap<T, Connection<T>> connections = new LinkedMultiValueMap<>();
        left.iterate(connections::add);
        right.iterate((node, rightGraphsConnection) -> {
            List<Connection<T>> nodeConnections = connections.get(node);
            if (CollectionUtils.isEmpty(nodeConnections)) {
                throw new AssertionError(String.format(
                        "Could not find any rightGraphsConnection %s from node %s in left graph.\nInspected graphs:\n%s\n%s\n",
                        rightGraphsConnection, node, left, right
                ));
            }
            int i = nodeConnections.indexOf(rightGraphsConnection);
            if (i == -1) {
                throw new AssertionError(String.format(
                        "Left graph does not contain rightGraphsConnection %s from node %s. Inspected graphs:\n%s\n%s\n",
                        rightGraphsConnection, node, left, right
                ));
            }
            Connection<T> leftGraphsConnection = nodeConnections.get(i);
            if (leftGraphsConnection.getWeight() != rightGraphsConnection.getWeight()) {
                throw new AssertionError(String.format(
                        "Left and right graph's connections %s and %s have different weights.\nInspected graphs:\n%s\n%s\n",
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
                    "Left graph contains elements which are not present in the right one:\n%s", connections
            ));
        }
    }
}

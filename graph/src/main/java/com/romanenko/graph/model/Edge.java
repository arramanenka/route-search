package com.romanenko.graph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Edge<T> implements Comparable<Edge<T>> {

    private final Connection<T> connection;

    @EqualsAndHashCode.Exclude
    private T owner;

    @EqualsAndHashCode.Exclude
    private int overallWeight;

    @Override
    public int compareTo(Edge<T> tEdge) {
        return Integer.compare(this.overallWeight, tEdge.overallWeight);
    }
}

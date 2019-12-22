package com.romanenko.routefinder.graph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GraphConnection<T> implements Comparable<GraphConnection<T>> {

    private final Connection<T> connection;

    @EqualsAndHashCode.Exclude
    private T owner;

    @EqualsAndHashCode.Exclude
    private int overallWeight;

    @Override
    public int compareTo(GraphConnection<T> tGraphConnection) {
        return Integer.compare(this.overallWeight, tGraphConnection.overallWeight);
    }
}

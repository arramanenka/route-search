package com.romanenko.routefinder.graph.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Connection<T> implements Comparable<Connection<T>>{

    private final T connectedInstance;
    /**
     * Weight of the connection to this node
     */
    @EqualsAndHashCode.Exclude
    private final int weight;

    @Override
    public int compareTo(Connection<T> o) {
        return Integer.compare(this.weight, o.weight);
    }
}

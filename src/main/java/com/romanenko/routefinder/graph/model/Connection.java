package com.romanenko.routefinder.graph.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Connection<T> {

    private final T instance;
    /**
     * Weight of the connection to this node
     */
    @EqualsAndHashCode.Exclude
    private final int weight;
}

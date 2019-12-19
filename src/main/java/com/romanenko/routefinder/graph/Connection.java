package com.romanenko.routefinder.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Connection<T> {
    private final T instance;
    @EqualsAndHashCode.Exclude
    private int weight;
}

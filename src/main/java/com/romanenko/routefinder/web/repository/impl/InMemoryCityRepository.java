package com.romanenko.routefinder.web.repository.impl;

import com.romanenko.routefinder.graph.impl.CachedGraph;
import com.romanenko.routefinder.web.repository.CityRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Repository
@Log4j2
@ConditionalOnProperty(name = "city.repository", havingValue = "in_memory")
public class InMemoryCityRepository implements CityRepository {

    private final CachedGraph<String> cities = new CachedGraph<>();
    @Value("${city.repository.max.cached.value:5000}")
    private int maxCachedWeight;

    @Override
    public void add(String city, String connectedCity, int meters) {
        cities.add(city, connectedCity, meters);
    }

    @Override
    public void remove(String city) {
        cities.remove(city);
    }

    @Override
    public Collection<String> getReachableCities(String city, int maximumDistance) {
        return cities.getReachableNodes(city, maximumDistance);
    }

    @PostConstruct
    public void init() {
        if (maxCachedWeight < 0) {
            maxCachedWeight = 0;
        }
        log.info("Initialized in memory city repository with max cache size " + maxCachedWeight);
        cities.setMaxDistanceCached(maxCachedWeight);
    }
}

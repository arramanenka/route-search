package com.romanenko.routefinder.repository.impl;

import com.romanenko.routefinder.dao.CityDao;
import com.romanenko.routefinder.dao.impl.neo4j.model.CityConnection;
import com.romanenko.routefinder.graph.impl.CachedGraph;
import com.romanenko.routefinder.graph.impl.UnbalancedGraph;
import com.romanenko.routefinder.repository.CityRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Log4j2
@Repository
@ConditionalOnProperty(name = "city.repository.type", havingValue = "in_memory")
public class InMemoryCityRepository implements CityRepository {

    @Value("${city.repository.maxCachedValue:1440}")
    private int maxCachedWeight;
    private CachedGraph<String> cities;

    @Override
    public Collection<String> getReachableCities(String city, int maximumMinutes) {
        return cities.getReachableNodes(city, maximumMinutes);
    }

    @PostConstruct
    public void init() {
        if (maxCachedWeight < 0) {
            maxCachedWeight = 0;
        }
        log.info("Initialized in memory city repository with max cache size " + maxCachedWeight);
        cities.setMaxCachedWeight(maxCachedWeight);
    }

    @Autowired
    public void loadCities(CityDao cityDao) {
        UnbalancedGraph<String> cityGraph = new UnbalancedGraph<>();
        for (CityConnection cityConnection : cityDao.findAllRoadConnections()) {
            cityGraph.add(cityConnection.getFromCity().getCityName(), cityConnection.getToCity().getCityName(), cityConnection.getTime());
            log.info(cityConnection);
        }
        this.cities = new CachedGraph<>(cityGraph);
    }
}

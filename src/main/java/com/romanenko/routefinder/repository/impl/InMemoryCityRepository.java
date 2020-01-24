package com.romanenko.routefinder.repository.impl;

import com.romanenko.routefinder.dao.CityDao;
import com.romanenko.routefinder.dao.impl.neo4j.model.CityConnection;
import com.romanenko.routefinder.graph.algo.GraphAlgorithms;
import com.romanenko.routefinder.graph.impl.SortedGraph;
import com.romanenko.routefinder.repository.CityRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Log4j2
@Repository
@ConditionalOnProperty(name = "city.repository.type", havingValue = "in_memory")
public class InMemoryCityRepository implements CityRepository {
    private SortedGraph<String> cities;

    @Override
    public Collection<String> getReachableCities(String city, int maximumMinutes) {
        return GraphAlgorithms.bfs(cities, city, maximumMinutes);
    }

    @Autowired
    public void loadCities(CityDao cityDao) {
        SortedGraph<String> cityGraph = new SortedGraph<>();
        int connectionCount = 0;
        for (CityConnection cityConnection : cityDao.findAllRoadConnections()) {
            cityGraph.add(
                    cityConnection.getFromCity().getCityName(),
                    cityConnection.getToCity().getCityName(),
                    cityConnection.getTime()
            );
            connectionCount++;
        }
        log.info("Loaded " + connectionCount + " connections");
        this.cities = cityGraph;
    }
}

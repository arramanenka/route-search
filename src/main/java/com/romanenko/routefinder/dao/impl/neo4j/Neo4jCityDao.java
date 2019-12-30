package com.romanenko.routefinder.dao.impl.neo4j;

import com.romanenko.routefinder.dao.CityDao;
import com.romanenko.routefinder.dao.impl.neo4j.model.City;
import com.romanenko.routefinder.dao.impl.neo4j.model.CityConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@ConditionalOnProperty(name = "city.dao.type", havingValue = "neo4j")
@RequiredArgsConstructor
public class Neo4jCityDao implements CityDao {

    private final CityRepo cityRepo;
    private final CityConnectionRepo cityConnectionRepo;

    @Override
    public Collection<City> findAllReachableCities(String startCityName, int maximumMinutes) {
        return cityRepo.findAllCitiesAround(startCityName, maximumMinutes);
    }

    @Override
    public Iterable<CityConnection> findAllRoadConnections() {
        return cityConnectionRepo.findAll();
    }
}

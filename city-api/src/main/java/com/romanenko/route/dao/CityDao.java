package com.romanenko.route.dao;

import com.romanenko.route.dao.impl.neo4j.model.City;
import com.romanenko.route.dao.impl.neo4j.model.CityConnection;

import java.util.Collection;

public interface CityDao {

    Collection<City> findAllReachableCities(String startCityName, int maximumMinutes);

    Iterable<CityConnection> findAllRoadConnections();
}
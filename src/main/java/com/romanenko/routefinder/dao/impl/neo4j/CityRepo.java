package com.romanenko.routefinder.dao.impl.neo4j;

import com.romanenko.routefinder.dao.impl.neo4j.model.City;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;

public interface CityRepo extends Neo4jRepository<City, Long> {
    /**
     * Please note, that of version 3.5.14 this algorithm will fail if directly connected cities are connected with
     * connections that cost more than indirect connections.
     */
    @Query("Match(n:City {name:{0}}) " +
            "call algo.bfs.stream(\"City\", \"CityConnection\", \"BOTH\", id(n), {maxCost:{1}, weightProperty:'time'}) " +
            "yield nodeIds " +
            "unwind nodeIds as nodeId " +
            "return algo.asNode(nodeId)")
    Collection<City> findAllCitiesAround(String startCityName, int maximumMinutes);
}

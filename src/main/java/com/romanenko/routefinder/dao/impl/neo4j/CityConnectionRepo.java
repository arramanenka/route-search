package com.romanenko.routefinder.dao.impl.neo4j;

import com.romanenko.routefinder.dao.impl.neo4j.model.CityConnection;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CityConnectionRepo extends Neo4jRepository<CityConnection, Long> {
}

package com.romanenko.routefinder.dao.impl.neo4j.model;

import lombok.Data;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@Data
@RelationshipEntity(type = "CityConnection")
public class CityConnection {

    private int time;

    @StartNode
    private City fromCity;

    @EndNode
    private City toCity;
}

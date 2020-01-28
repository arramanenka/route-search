package com.romanenko.route.dao.impl.neo4j.model;

import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Data
@NodeEntity(label = "City")
public class City {

    @Property(name = "name")
    private String cityName;
}

package com.romanenko.routefinder.repository.impl;

import com.romanenko.routefinder.dao.CityDao;
import com.romanenko.routefinder.dao.impl.neo4j.model.City;
import com.romanenko.routefinder.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Collectors;

@Log4j2
@Repository
@ConditionalOnProperty(name = "city.repository.type", havingValue = "in_db")
@RequiredArgsConstructor
public class DbCityRepository implements CityRepository {

    private final CityDao cityDao;

    @Override
    public Collection<String> getReachableCities(String city, int maximumMinutes) {
        return cityDao.findAllReachableCities(city, maximumMinutes)
                .stream()
                .map(City::getCityName)
                .collect(Collectors.toSet());
    }
}

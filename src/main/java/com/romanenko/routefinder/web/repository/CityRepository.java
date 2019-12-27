package com.romanenko.routefinder.web.repository;

import java.util.Collection;

public interface CityRepository {
    void add(String city, String connectedCity, int meters);

    void remove(String city);

    Collection<String> getReachableCities(String city, int maximumDistance);
}

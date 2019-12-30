package com.romanenko.routefinder.repository;

import java.util.Collection;

public interface CityRepository {

    Collection<String> getReachableCities(String city, int maximumMinutes);
}

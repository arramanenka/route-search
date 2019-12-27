package com.romanenko.routefinder.web.service;

import com.romanenko.routefinder.web.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public void add(String city, String connectedCity, int meters) {
        cityRepository.add(city, connectedCity, meters);
    }

    public void remove(String city) {
        cityRepository.remove(city);
    }

    public Collection<String> getReachableCities(String city, int maximumDistance) {
        return cityRepository.getReachableCities(city, maximumDistance);
    }
}

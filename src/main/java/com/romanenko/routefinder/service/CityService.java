package com.romanenko.routefinder.service;

import com.romanenko.routefinder.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public Collection<String> getReachableCities(String city, int maximumMinutes) {
        if (maximumMinutes <= 0 || StringUtils.isEmpty(city)) {
            return Collections.emptyList();
        }
        return cityRepository.getReachableCities(city, maximumMinutes);
    }
}

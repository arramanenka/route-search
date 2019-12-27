package com.romanenko.routefinder.web.controller;

import com.romanenko.routefinder.web.model.CityConnection;
import com.romanenko.routefinder.web.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class CommuteRangeController {
    private final CityService cityService;

    @PostMapping("city")
    public void addCityConnection(@Valid @RequestBody CityConnection cityConnection) {
        cityService.add(cityConnection.getFirstCity(), cityConnection.getSecondCity(), cityConnection.getDistance());
    }

    @DeleteMapping("city")
    public void deleteCityConnection(@RequestBody String city) {
        cityService.remove(city);
    }

    @GetMapping("cities")
    public Collection<String> getReachableCities(@RequestParam String cityName, @RequestParam int maxDistance) {
        return cityService.getReachableCities(cityName, maxDistance);
    }

}

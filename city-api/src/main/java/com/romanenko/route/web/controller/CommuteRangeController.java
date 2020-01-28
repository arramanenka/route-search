package com.romanenko.route.web.controller;

import com.romanenko.route.service.CityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "range")
public class CommuteRangeController {
    private final CityService cityService;

    @ApiOperation(
            value = "getReachableCities", notes = "Given starting city and maximum minutes to travel, " +
            "determine which cities are reachable.\n" +
            "E.g.Assume there are cities 1, 2 and 3\n" +
            "Assume that it takes 20 minutes to get from city 1 to 2; 40 minutes to get from city 1 to 3\n" +
            "This method will return:\n" +
            "from city 1:\n" +
            "[2] for 20-39 minutes;\n" +
            "[2, 3] for 40+ minutes;\n" +
            "from city 3:\n" +
            "[1] for 40-59 minutes;\n" +
            "[1, 2] for 60+ minutes;\n"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of reachable cities")
    })
    //todo make controllers reactive
    @GetMapping(value = "cities", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<String> getReachableCities(@RequestParam String cityName, @RequestParam int maximumMinutes) {
        return cityService.getReachableCities(cityName, maximumMinutes);
    }

}

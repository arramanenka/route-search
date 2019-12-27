package com.romanenko.routefinder.web.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class CityConnection {
    @NotEmpty(message = "First city should not be null")
    private String firstCity;
    @NotEmpty(message = "Second city should not be null")
    private String secondCity;
    @Min(message = "Distance between cities should be > 0", value = 0)
    private int distance;
}

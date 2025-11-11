package com.paxier.spring_modulith_demo.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponse(
    String name,
    Main main,
    List<Weather> weather,
    Wind wind
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Main(
        double temp,
        double feels_like,
        double temp_min,
        double temp_max,
        int pressure,
        int humidity
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Weather(
        String main,
        String description,
        String icon
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wind(
        double speed,
        int deg
    ) {}
}


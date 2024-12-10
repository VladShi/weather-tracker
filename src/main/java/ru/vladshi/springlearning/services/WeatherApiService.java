package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.dto.LocationDto;

import java.util.List;

public interface WeatherApiService {

    List<LocationDto> getLocationsByName(String geoName, Integer userMaxLocations);

    List<LocationDto> getLocationsByName(String geoName);
}

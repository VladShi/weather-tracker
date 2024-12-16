package ru.vladshi.springlearning.services;

import ru.vladshi.springlearning.dto.LocationDto;
import ru.vladshi.springlearning.dto.WeatherDto;
import ru.vladshi.springlearning.entities.Location;

import java.util.List;

public interface WeatherApiService {

    List<LocationDto> getLocationsByName(String geoName, int userMaxLocations);

    List<LocationDto> getLocationsByName(String locationName);

    List<WeatherDto> getWeathers(List<Location> locations);
}

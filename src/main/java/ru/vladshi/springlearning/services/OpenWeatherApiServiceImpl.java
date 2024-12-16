package ru.vladshi.springlearning.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.vladshi.springlearning.dto.LocationDto;
import ru.vladshi.springlearning.dto.WeatherDto;
import ru.vladshi.springlearning.entities.Location;
import ru.vladshi.springlearning.exceptions.OpenWeatherException;
import ru.vladshi.springlearning.exceptions.OpenWeatherUnauthorizedException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@AllArgsConstructor
public class OpenWeatherApiServiceImpl implements WeatherApiService {

    private final RestClient restClient;

    @Value("${openweather.api.base-url}")
    private String baseUrl;
    @Value("${openweather.api.geocoding-url}")
    private String geocodingUrl;
    @Value("${openweather.api.max-locations}")
    private int defaultMaxLocations;
    @Value("${openweather.api.weather-url}")
    private String weatherUrl;
    @Value("${openweather.api.key}")
    private String apiKey;
    
    @Override
    public List<LocationDto> getLocationsByName(String locationName, int userMaxLocations) { // TODO реализовать во вьюхах работу с использованием userMaxLocations
        int maxLocations = (userMaxLocations > 0) ? userMaxLocations : defaultMaxLocations;
        String uri = baseUrl + geocodingUrl.formatted(locationName, maxLocations, apiKey);
        return restClient.get()                                 // TODO перехват для всех restClient, try-catch или в глобальном перехватчике ResourceAccessException
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, (request, response) -> {
                    throw new OpenWeatherUnauthorizedException("Please write proper API key"); // TODO добавить обработку ошибки
                })
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), ((request, response) -> {
                    throw new OpenWeatherException(
                            "Openweathermap server or network error. Status code: " + response.getStatusCode()); // TODO добавить обработку ошибки
                }))
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public List<LocationDto> getLocationsByName(String locationName) {
        return getLocationsByName(locationName, defaultMaxLocations);
    }

    @Override
    public List<WeatherDto> getWeathers(List<Location> locations) {

        List<WeatherDto> weathers = new ArrayList<>();

        if (locations == null || locations.isEmpty()) {
            return weathers;
        }

        for (Location location : locations) {
            WeatherDto weather = getWeather(location);
            weathers.add(weather);
        }
        return weathers;
    }

    private WeatherDto getWeather(Location location) {
        String uri = baseUrl + weatherUrl.formatted(location.getLatitude(), location.getLongitude(), apiKey);
        WeatherDto weather = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, (request, response) -> {
                    throw new OpenWeatherUnauthorizedException(
                            "Please write proper API key for Openweathermap service");}) // TODO добавить обработку ошибки
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError()
                        , ((request, response) -> { throw new OpenWeatherException(
                                "Openweathermap server or network error. Status code: " + response.getStatusCode());})) // TODO добавить обработку ошибки
                .body(WeatherDto.class);

        if (weather == null) {
            throw new OpenWeatherException("Openweathermap error. Cannot get weather data");
        }

        weather.setLocationName(location.getName());
        weather.setLocationId(location.getId());

        return weather;
    }
}

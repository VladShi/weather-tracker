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
    public List<LocationDto> getLocationsByName(String geoName, int userMaxLocations) {
        int maxLocations = (userMaxLocations > 0) ? userMaxLocations : defaultMaxLocations;
        String uri = baseUrl + geocodingUrl.formatted(geoName, maxLocations, apiKey);
        return restClient.get()
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
    public List<LocationDto> getLocationsByName(String geoName) {
        return getLocationsByName(geoName, defaultMaxLocations);
    }

    @Override
    public List<WeatherDto> getWeathers(List<Location> locations) {

        List<WeatherDto> weathers = new ArrayList<>();

        if (locations == null || locations.isEmpty()) {
            return weathers;
        }

        for (Location location : locations) {
            String uri = baseUrl + weatherUrl.formatted(location.getLatitude(), location.getLongitude(), apiKey);
            weathers.add(
                    restClient.get()
                        .uri(uri)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(HttpStatus.UNAUTHORIZED::equals, (request, response) -> {
                            throw new OpenWeatherUnauthorizedException("Please write proper API key"); // TODO добавить обработку ошибки
                        })
                        .onStatus(status -> status.is4xxClientError() || status.is5xxServerError()
                                , ((request, response) -> { throw new OpenWeatherException(
                                    "Openweathermap server or network error. Status code: " + response.getStatusCode()); // TODO добавить обработку ошибки
                        }))
                        .body(WeatherDto.class)
            );
        }
        return weathers;
    }
}

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
import ru.vladshi.springlearning.exceptions.OpenWeatherException;
import ru.vladshi.springlearning.exceptions.OpenWeatherUnauthorizedException;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@AllArgsConstructor
public class OpenWeatherApiServiceImpl implements WeatherApiService {

    private final RestClient restClient;

    @Value("${openweather.api.base-url}")
    private String baseUrl;
    @Value("${openweather.api.geocoding}")
    private String geocodingUrl;
    @Value("${openweather.api.max-locations}")
    private int defaultMaxLocations;
    @Value("${openweather.api.key}")
    private String apiKey;
    
    @Override
    public List<LocationDto> getLocationsByName(String geoName, Integer userMaxLocations) {
        int maxLocations = (userMaxLocations != null) ? userMaxLocations : defaultMaxLocations;
        String uri = baseUrl + String.format(geocodingUrl, geoName, maxLocations, apiKey);
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

    // метод по получению прогноза по координатам

}

package ru.vladshi.springlearning.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenWeatherApiServiceImpl implements weatherApiService {

    private final RestClient restClient;

    @Autowired
    public OpenWeatherApiServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    // метод по получению координат по названию

    // метод по получению прогноза по координатам

}

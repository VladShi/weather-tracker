package ru.vladshi.springlearning.services;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestClient;
import ru.vladshi.springlearning.dto.LocationDto;
import ru.vladshi.springlearning.dto.WeatherDto;
import ru.vladshi.springlearning.entities.Location;
import ru.vladshi.springlearning.exceptions.OpenWeatherException;
import ru.vladshi.springlearning.exceptions.OpenWeatherUnauthorizedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class OpenWeatherApiServiceImplTest {

    private static final String GEOCODING_URL = "/geo/1.0/direct?q=%s&limit=%d&appid=%s";
    private static final int DEFAULT_MAX_LOCATIONS = 5;
    private static final String WEATHER_URL = "/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric";
    private static final String API_KEY = "test-api-key";
    private static final String GEOCODING_PATH_PATTERN = GEOCODING_URL
            .replaceAll("\\?.*", ".*"); // Заменяем все параметры форматирования на .*
    private static final String WEATHER_PATH_PATTERN = WEATHER_URL.replaceAll("\\?.*", ".*");

    private static WeatherApiService openWeatherApiService;
    private static Location testLocation;

    @BeforeAll
    static void setUp(WireMockRuntimeInfo wireMockRuntimeInfo) {
        String baseUrl = wireMockRuntimeInfo.getHttpBaseUrl();

        RestClient restClient = RestClient.create();

        openWeatherApiService = new OpenWeatherApiServiceImpl(
                restClient, baseUrl, GEOCODING_URL, DEFAULT_MAX_LOCATIONS, WEATHER_URL, API_KEY);

        testLocation = new Location();
        testLocation.setLatitude(new BigDecimal("55.7558"));
        testLocation.setLongitude(new BigDecimal("37.6173"));
    }

    @Test
    void testGetLocationsByName_WhenStatusIs200_ShouldReturnLocations() {
        String jsonResponse = """
                [
                    {
                        "name":"Moscow",
                        "local_names":{
                            "ru":"Москва"
                        },
                        "lat":55.7558,
                        "lon":37.6173,
                        "country":"RU",
                        "state":"Moscow City"
                    },
                    {
                        "name":"Moscow",
                        "local_names":{
                            "en":"Moscow"
                        },
                        "lat":39.1031,
                        "lon":-84.5120,
                        "country":"US",
                        "state":"Ohio"
                    }
                ]
                """;
        stubFor(get(urlPathMatching(GEOCODING_PATH_PATTERN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        List<LocationDto> result = openWeatherApiService.getLocationsByName("Moscow");

        assertEquals(2, result.size());

        LocationDto moscowRU = result.get(0);
        assertEquals("Moscow", moscowRU.getName());
        assertEquals(new BigDecimal("55.7558"), moscowRU.getLat());
        assertEquals(new BigDecimal("37.6173"), moscowRU.getLon());
        assertEquals("RU", moscowRU.getCountry());
        assertEquals("Moscow City", moscowRU.getState());

        LocationDto moscowUS = result.get(1);
        assertEquals("Moscow", moscowUS.getName());
        assertEquals(new BigDecimal("39.1031"), moscowUS.getLat());
        assertEquals(new BigDecimal("-84.5120"), moscowUS.getLon());
        assertEquals("US", moscowUS.getCountry());
        assertEquals("Ohio", moscowUS.getState());
    }

    @Test
    void testGetWeathers_WhenStatusIs200_ShouldReturnWeather() {
        String jsonResponse = """
                {
                   "coord": {
                     "lon": 37.6173,
                     "lat": 55.7558
                   },
                   "weather": [
                     {
                       "id": 804,
                       "main": "Clouds",
                       "description": "overcast clouds",
                       "icon": "04d"
                     }
                   ],
                   "base": "stations",
                   "main": {
                     "temp": -3.88,
                     "feels_like": -9.35,
                     "temp_min": -4.71,
                     "temp_max": -3.12,
                     "pressure": 1015,
                     "humidity": 70,
                     "sea_level": 1015,
                     "grnd_level": 995
                   },
                   "visibility": 10000,
                   "wind": {
                     "speed": 4.39,
                     "deg": 249,
                     "gust": 11.83
                   },
                   "clouds": {
                     "all": 91
                   },
                   "dt": 1733909343,
                   "sys": {
                     "type": 2,
                     "id": 2094500,
                     "country": "RU",
                     "sunrise": 1733896165,
                     "sunset": 1733921795
                   },
                   "timezone": 10800,
                   "id": 524901,
                   "name": "Moscow",
                   "cod": 200
                }
                """;

        stubFor(get(urlPathMatching(WEATHER_PATH_PATTERN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        List<Location> locations = List.of(testLocation);

        List<WeatherDto> weathers = openWeatherApiService.getWeathers(locations);

        assertEquals(1, weathers.size());
        WeatherDto weather = weathers.get(0);
        assertNull(weather.getLocationName());
        assertEquals("-4", weather.getTemperature());
        assertEquals("-9", weather.getFeelsLike());
        assertEquals("70", weather.getHumidity());
        assertEquals("Overcast clouds", weather.getSkyInfo());
        assertEquals("04d", weather.getIconName());
        assertEquals("RU", weather.getCountryCode());
        // Добавь дополнительные проверки для других полей, если это необходимо
    }

    static Stream<Arguments> statusCodeProvider() {
        return Stream.of(
                Arguments.of(401, OpenWeatherUnauthorizedException.class),
                Arguments.of(500, OpenWeatherException.class),
                Arguments.of(403, OpenWeatherException.class),
                Arguments.of(404, OpenWeatherException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("statusCodeProvider")
    void testGetLocationsByName_ThrowsException_ForVariousStatusCodes(int statusCode,
                                                                      Class<? extends Exception> expectedException) {
        stubFor(get(urlPathMatching(GEOCODING_PATH_PATTERN))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")));

        assertThrows(expectedException, () -> openWeatherApiService.getLocationsByName("Moscow"));
    }

    @ParameterizedTest
    @MethodSource("statusCodeProvider")
    void testGetWeathers_ThrowsException_ForVariousStatusCodes(int statusCode,
                                                               Class<? extends Exception> expectedException) {

        stubFor(get(urlPathMatching(WEATHER_PATH_PATTERN))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")));

        List<Location> locations = List.of(testLocation);

        assertThrows(expectedException, () -> openWeatherApiService.getWeathers(locations));
    }

//    @Mock
//    private RestClient restClient;
//
//    @Mock
//    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
//
//    @Mock
//    private RestClient.ResponseSpec responseSpec;
//
//    private WeatherApiService service;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        objectMapper = new ObjectMapper();
//
//        RestClient.Builder builder = mock(RestClient.Builder.class);
//        when(builder.build()).thenReturn(restClient);
//
//        service = new OpenWeatherApiServiceImpl(
//                restClient,
//                "https://api.openweathermap.org", // baseUrl
//                "/geo/1.0/direct?q=%s&limit=%d&appid=%s", // geocodingUrl
//                "test-api-key" // apiKey
//        );
//
//        // Настройка цепочки вызовов Mockito
//        when(restClient.get()).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersUriSpec);
//        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
//    }
//
//    @Test
//    void testGetLocationsByName() throws Exception {
//        // Подготовка JSON-строки, имитирующей ответ от OpenWeatherMap API
//        String jsonResponse = """
//                [
//                    {
//                        "name":"Moscow",
//                        "local_names":
//                            {
//                                "ru":"Москва"
//                            },
//                        "lat":55.7558,
//                        "lon":37.6173,
//                        "country":"RU",
//                        "state":"Moscow City"
//                    },
//                    {
//                        "name":"Moscow",
//                        "local_names":
//                            {
//                                "en":"Moscow"
//                            },
//                        "lat":39.1031,
//                        "lon":-84.5120,
//                        "country":"US",
//                        "state":"Ohio"
//                    }
//                ]
//        """;
//
//        // Настройка возврата мок-данных
//        List<LocationDto> locations = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
//        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(locations);
//
//        // Вызов метода и проверка результата
//        List<LocationDto> result = service.getLocationsByName("Moscow");
//
//        assertEquals(2, result.size());
//
//        // Проверка первого города (Россия)
//        LocationDto moscowRU = result.get(0);
//        assertEquals("Moscow", moscowRU.getName());
//        assertEquals(new BigDecimal("55.7558"), moscowRU.getLat());
//        assertEquals(new BigDecimal("37.6173"), moscowRU.getLon());
//        assertEquals("RU", moscowRU.getCountry());
//        assertEquals("Moscow City", moscowRU.getState());
//
//        // Проверка второго города (США)
//        LocationDto moscowUS = result.get(1);
//        assertEquals("Moscow", moscowUS.getName());
//        assertEquals(new BigDecimal("39.1031"), moscowUS.getLat());
//        assertEquals(new BigDecimal("-84.5120"), moscowUS.getLon());
//        assertEquals("US", moscowUS.getCountry());
//        assertEquals("Ohio", moscowUS.getState());
//    }
}

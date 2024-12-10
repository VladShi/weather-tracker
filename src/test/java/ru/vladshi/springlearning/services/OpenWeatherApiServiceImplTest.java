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
import ru.vladshi.springlearning.exceptions.OpenWeatherException;
import ru.vladshi.springlearning.exceptions.OpenWeatherUnauthorizedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class OpenWeatherApiServiceImplTest {

    private static final String GEOCODING_URL = "/geo/1.0/direct?q=%s&limit=%d&appid=%s";
    private static final int DEFAULT_MAX_LOCATIONS = 5;
    private static final String API_KEY = "test-api-key";
    private static final String GEOCODING_PATH_PATTERN = GEOCODING_URL
            .replaceAll("\\?.*", ".*"); // Заменяем все параметры форматирования на .*

    private static WeatherApiService openWeatherApiService;

    @BeforeAll
    static void setUp(WireMockRuntimeInfo wireMockRuntimeInfo) {
        String baseUrl = wireMockRuntimeInfo.getHttpBaseUrl();

        RestClient restClient = RestClient.create();

        openWeatherApiService = new OpenWeatherApiServiceImpl(
                restClient, baseUrl, GEOCODING_URL, DEFAULT_MAX_LOCATIONS, API_KEY);
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

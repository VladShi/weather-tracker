package ru.vladshi.springlearning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter @Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDto {

    @JsonIgnoreProperties
    private String locationName;

    @JsonIgnoreProperties
    private int locationId;

    @JsonProperty("main")
    private Main main;

    @Setter @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private BigDecimal temp;
    }

    public BigDecimal getTemperature() {
        return main != null ? main.getTemp() : BigDecimal.ZERO;
    }
}

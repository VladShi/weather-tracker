package ru.vladshi.springlearning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Setter @Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationDto {

    private String name;
    private BigDecimal lat;
    private BigDecimal lon;
    private String country;
    private String state;

    public void setLat(BigDecimal latitude) {
        this.lat = latitude.setScale(7, RoundingMode.HALF_UP);
    }

    public void setLon(BigDecimal longitude) {
        this.lon = longitude.setScale(7, RoundingMode.HALF_UP);
    }
}

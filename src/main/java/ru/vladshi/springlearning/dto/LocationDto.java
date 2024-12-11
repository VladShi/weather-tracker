package ru.vladshi.springlearning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter @Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationDto {

    private String name;
    private BigDecimal lat;
    private BigDecimal lon;
    private String country;
    private String state;
}

package ru.vladshi.springlearning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDto {

    @JsonIgnoreProperties
    private String locationName;

    @JsonIgnoreProperties
    private int locationId;

    @JsonProperty("main")
    private Main main;

    @JsonProperty("weather")
    private List<Weather> weather;

    @JsonProperty("sys")
    private LocationInfo locationInfo;

    @Setter @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private float temp;
        @JsonProperty("feels_like")
        private float feelsLike;
        private int humidity;
    }

    @Setter @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String description;
        private String icon;
    }

    @Setter @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationInfo {
        @JsonProperty("country")
        private String countryCode;
    }

    public String getTemperature() {
        if (main == null) {
            return "";
        }
        int temp = Math.round(main.getTemp());
        return String.format("%s%s", temp > 0 ? "+": "", temp);
    }

    public String getFeelsLike() {
        if (main == null) {
            return "";
        }
        int temp = Math.round(main.getFeelsLike());
        return String.format("%s%s", temp > 0 ? "+" : "", temp);
    }

    public String getHumidity() {
        if (main == null) {
            return "";
        }
        return String.valueOf(main.getHumidity());
    }

    public String getSkyInfo() {
        if (weather == null) {
            return "";
        }
        String info = weather.getFirst().getDescription();
        char firstChar = Character.toUpperCase(info.charAt(0));
        return firstChar + info.substring(1);
    }

    public String getIconName() {
        if (weather == null) {
            return "";
        }
        return weather.getFirst().getIcon();
    }

    public String getCountryCode() {  // TODO возможно стоит так же выводить инфо об области. Так как в одной стране могут быть населенные пункты с одинаковым названием
        if (locationInfo == null) {
            return "";
        }
        return locationInfo.getCountryCode();
    }
}

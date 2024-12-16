package ru.vladshi.springlearning.mappers;

import ru.vladshi.springlearning.dto.LocationDto;
import ru.vladshi.springlearning.entities.Location;

public class DtoMapper {

    public static Location toEntity(LocationDto dto) {
        Location location = new Location();

        location.setName(dto.getName());
        location.setLatitude(dto.getLat());
        location.setLongitude(dto.getLon());
        return location;
    }
}

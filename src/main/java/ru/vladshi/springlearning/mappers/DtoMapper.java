package ru.vladshi.springlearning.mappers;

import ru.vladshi.springlearning.dto.LocationDto;
import ru.vladshi.springlearning.dto.UserDto;
import ru.vladshi.springlearning.entities.Location;
import ru.vladshi.springlearning.entities.User;

public class DtoMapper {

    public static Location toEntity(LocationDto dto) {
        Location location = new Location();

        location.setName(dto.getName());
        location.setLatitude(dto.getLat());
        location.setLongitude(dto.getLon());
        return location;
    }

    public static User toEntity(UserDto dto) {
        User user = new User();

        user.setLogin(dto.getLogin());
        user.setPassword(dto.getPassword());
        return user;
    }
}

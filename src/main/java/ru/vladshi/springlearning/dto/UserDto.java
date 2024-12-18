package ru.vladshi.springlearning.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UserDto {
    private String login;
    private String password;
    private String confirmPassword;
}

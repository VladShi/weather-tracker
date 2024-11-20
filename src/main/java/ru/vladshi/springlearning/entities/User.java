package ru.vladshi.springlearning.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "user")
@Getter @Setter @NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "login", unique = true, nullable = false)
    @Size(min = 5, max = 50, message = "Login should be between 5 and 50 characters")
    @NotNull(message = "Login is required")
    private String login;

    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters")
    @NotNull(message = "Password is required")
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_location",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id"))
    private List<Location> locations;
}

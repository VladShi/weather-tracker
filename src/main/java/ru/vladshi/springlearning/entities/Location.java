package ru.vladshi.springlearning.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "location", uniqueConstraints = {@UniqueConstraint(columnNames = {"latitude", "longitude"})})
@Getter @Setter @NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "latitude", nullable = false, precision = 6, scale = 3)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 6, scale = 3)
    private BigDecimal longitude;

    @ManyToMany(mappedBy = "locations")
    private List<User> users;
}

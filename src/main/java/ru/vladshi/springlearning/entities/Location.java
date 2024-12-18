package ru.vladshi.springlearning.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter @Getter
@EqualsAndHashCode(exclude = {"id", "users"})
@Table(name = "location", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "latitude", "longitude"})})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @ManyToMany(mappedBy = "locations")
    private List<User> users;

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude.setScale(7, RoundingMode.HALF_UP);
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude.setScale(7, RoundingMode.HALF_UP);
    }
}

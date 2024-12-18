package ru.vladshi.springlearning.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "location", uniqueConstraints = {@UniqueConstraint(columnNames = {"latitude", "longitude"})})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    @EqualsAndHashCode.Exclude
    private String name;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @ManyToMany(mappedBy = "locations")
    @EqualsAndHashCode.Exclude
    private List<User> users;

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude.setScale(7, RoundingMode.HALF_UP);
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude.setScale(7, RoundingMode.HALF_UP);
    }
}

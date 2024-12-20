package ru.vladshi.springlearning.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter @Getter
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;

        // These rounding is necessary because on the external weather api, some locations have slightly different
        // coordinates when requested in different languages. Although in fact it is the same location.
        var roundedLatitude = latitude.setScale(3, RoundingMode.HALF_UP);
        var roundedLongitude = longitude.setScale(3, RoundingMode.HALF_UP);

        var roundedLocationLatitude = location.latitude.setScale(3, RoundingMode.HALF_UP);
        var roundedLocationLongitude = location.longitude.setScale(3, RoundingMode.HALF_UP);

        return name.equals(location.name)
                && roundedLatitude.equals(roundedLocationLatitude) && roundedLongitude.equals(roundedLocationLongitude);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();

        result = 31 * result + latitude.setScale(3, RoundingMode.HALF_UP).hashCode();
        result = 31 * result + longitude.setScale(3, RoundingMode.HALF_UP).hashCode();
        return result;
    }
}

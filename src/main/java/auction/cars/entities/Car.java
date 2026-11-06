
package auction.cars.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "make", nullable = false, length = 100)
    private String make;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "mileage", nullable = false)
    private int mileage;

    @Column(name = "engine", length = 100)
    private String engine;

    @Column(name = "body_style", length = 50)
    private String bodyStyle;

    @Column(name = "exterior_color", length = 100)
    private String exteriorColor;

    @Column(name = "interior_color", length = 100)
    private String interiorColor;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Many cars belong to one owner
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}

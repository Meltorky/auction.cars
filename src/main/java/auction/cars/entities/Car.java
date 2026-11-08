
package auction.cars.entities;

import auction.cars.enums.BodyStyle;
import auction.cars.enums.CarReviewStatus;
import auction.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
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

    @Column(nullable = false, length = 100)
    private String make;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private int mileage;

    @Column(length = 100)
    private String engine;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_style", nullable = false, length = 30)
    private BodyStyle bodyStyle;

    @Column(name = "exterior_color", length = 50)
    private String exteriorColor;

    @Column(name = "interior_color", length = 50)
    private String interiorColor;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 30)
    private CarReviewStatus reviewStatus = CarReviewStatus.DRAFT;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;
}

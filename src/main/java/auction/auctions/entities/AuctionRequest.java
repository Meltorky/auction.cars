
package auction.auctions.entities;

import auction.auctions.enums.AuctionRequestStatus;
import auction.auctions.enums.AuctionType;
import auction.cars.entities.Car;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "auction_requests",
        indexes = {
                @Index(
                        name = "idx_request_car",
                        columnList = "car_id"
                ),
                @Index(
                        name = "idx_request_status_submitted",
                        columnList = "status, submitted_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_type", nullable = false, length = 20)
    private AuctionType requestedType;

    // Suggested by the owner
    @Positive
    @Column(name = "requested_reserve_price", precision = 15, scale = 2)
    private BigDecimal requestedReservePrice;

    // Suggested by admin if negotiation is needed
    @Positive
    @Column(name = "proposed_reserve_price", precision = 15, scale = 2)
    private BigDecimal proposedReservePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuctionRequestStatus status = AuctionRequestStatus.SUBMITTED;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @PrePersist
    protected void onCreate() {
        submittedAt = Instant.now();
    }
}

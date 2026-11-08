
package auction.bids.entities;

import auction.auctions.entities.Auction;
import auction.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "bids",
        indexes = {
                @Index(
                        name = "idx_bid_auction_amount",
                        columnList = "auction_id, amount"
                ),
                @Index(
                        name = "idx_bidder_placed_at",
                        columnList = "bidder_id, placed_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "placed_at", nullable = false, updatable = false)
    private Instant placedAt;

    @PrePersist
    protected void onCreate() {
        placedAt = Instant.now();
    }
}

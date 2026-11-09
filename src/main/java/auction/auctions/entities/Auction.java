
package auction.auctions.entities;

import auction.auctions.enums.AuctionStatus;
import auction.auctions.enums.AuctionType;
import auction.auctions.enums.AuctionResult;
import auction.cars.entities.Car;
import auction.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "auctions",
        indexes = {
                @Index(
                        name = "idx_auction_status_start",
                        columnList = "status, starts_at"
                ),
                @Index(
                        name = "idx_auction_status_end",
                        columnList = "status, ends_at"
                ),
                @Index(
                        name = "idx_auction_car",
                        columnList = "car_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // A car can have multiple auctions over time
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "auction_request_id",
            nullable = false,
            unique = true
    )
    private AuctionRequest auctionRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_type", nullable = false, length = 20)
    private AuctionType auctionType;

    @Column(name = "starting_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal startingPrice;

    // Null when auctionType = NO_RESERVE
    @Column(name = "reserve_price", precision = 15, scale = 2)
    private BigDecimal reservePrice;

    @Column(name = "minimum_bid_increment", nullable = false,
            precision = 15, scale = 2)
    private BigDecimal minimumBidIncrement = new BigDecimal("1000.00");

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuctionStatus status = AuctionStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuctionResult result = AuctionResult.NOT_DECIDED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column(precision = 19, scale = 2)
    private BigDecimal finalPrice;
}

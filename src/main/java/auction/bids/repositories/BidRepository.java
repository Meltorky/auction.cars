package auction.bids.repositories;

import auction.bids.entities.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    Optional<Bid> findTopByAuction_IdOrderByAmountDescPlacedAtDesc(
            UUID auctionId
    );

    List<Bid> findByAuction_IdOrderByAmountDesc(
            UUID auctionId
    );

    List<Bid> findByBidder_IdOrderByPlacedAtDesc(
            UUID bidderId
    );
}
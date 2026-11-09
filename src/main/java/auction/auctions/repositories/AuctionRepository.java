package auction.auctions.repositories;

import auction.auctions.entities.Auction;
import auction.auctions.enums.AuctionStatus;
import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    Optional<Auction> findById(UUID id);

    // Checks whether an auction has already been created from a request.
    boolean existsByAuctionRequest_Id(UUID requestId);

    // Retrieves auctions by status, ordered by their ending time.
    List<Auction> findByStatusOrderByEndsAtAsc(AuctionStatus status);

    // Checks whether a car has an auction in any of the supplied statuses.
    boolean existsByAuctionRequest_Car_IdAndStatusIn(
            UUID carId,
            List<AuctionStatus> statuses
    );
}
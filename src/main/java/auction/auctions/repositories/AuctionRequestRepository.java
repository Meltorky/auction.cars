package auction.auctions.repositories;

import auction.auctions.entities.AuctionRequest;
import auction.auctions.enums.AuctionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuctionRequestRepository
        extends JpaRepository<AuctionRequest, UUID> {

    List<AuctionRequest> findByStatusOrderBySubmittedAtAsc(
            AuctionRequestStatus status
    );

    List<AuctionRequest> findByCar_IdOrderBySubmittedAtDesc(
            UUID carId
    );

    boolean existsByCar_IdAndStatusIn(
            UUID carId,
            List<AuctionRequestStatus> statuses
    );
}
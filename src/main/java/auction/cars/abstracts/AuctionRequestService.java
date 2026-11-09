package auction.cars.abstracts;

import auction.auctions.dtos.AuctionRequestDto;
import auction.auctions.dtos.AuctionRequestResponseDto;
import auction.auctions.dtos.AuctionRequestUpdateDto;
import auction.users.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface AuctionRequestService {
    @Transactional
    AuctionRequestResponseDto submitRequest(
            AuctionRequestDto dto,
            User owner);

    @Transactional(readOnly = true)
    List<AuctionRequestResponseDto> getMyRequests(UUID ownerId);

    @Transactional
    void withdrawRequest(UUID requestId, User owner);

    // Updates a request after admin feedback and resubmits it for review.
    @Transactional
    AuctionRequestResponseDto updateAndResubmit(
            UUID requestId,
            AuctionRequestUpdateDto dto,
            User owner);
}

package auction.cars.abstracts;

import auction.auctions.dtos.AdminAuctionRequestResponseDto;
import auction.auctions.dtos.AuctionRequestApprovalDto;
import auction.auctions.dtos.AuctionRequestReviewDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface AdminAuctionRequestService {
    // Retrieves requests that are waiting for admin review.
    @Transactional(readOnly = true)
    List<AdminAuctionRequestResponseDto> getPendingRequests();

    // Moves a submitted request into the admin review process.
    @Transactional
    void startReview(UUID requestId);

    // Asks the owner to provide corrections before the request can proceed.
    @Transactional
    void requestChanges(
            UUID requestId,
            AuctionRequestReviewDto dto);

    // Rejects an auction request and records the admin's reason.
    @Transactional
    void rejectRequest(
            UUID requestId,
            AuctionRequestReviewDto dto);

    // Approves a request and stores the reserve price proposed by the admin.
    @Transactional
    void approveRequest(
            UUID requestId,
            AuctionRequestApprovalDto dto);
}


package auction.auctions.services;

import auction.auctions.dtos.AdminAuctionRequestResponseDto;
import auction.auctions.dtos.AuctionRequestApprovalDto;
import auction.auctions.dtos.AuctionRequestReviewDto;
import auction.auctions.entities.AuctionRequest;
import auction.auctions.enums.AuctionRequestStatus;
import auction.auctions.repositories.AuctionRequestRepository;
import auction.auctions.enums.AuctionType;
import auction.shared.HttpRequests.CustomResponseException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminAuctionRequestServiceImp implements auction.cars.abstracts.AdminAuctionRequestService {

    private final AuctionRequestRepository requestRepository;

    // Retrieves requests that are waiting for admin review.
    @Transactional(readOnly = true)
    @Override
    public List<AdminAuctionRequestResponseDto> getPendingRequests() {

        return requestRepository
                .findByStatusOrderBySubmittedAtAsc(
                        AuctionRequestStatus.SUBMITTED
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Moves a submitted request into the admin review process.
    @Transactional
    @Override
    public void startReview(UUID requestId) {

        AuctionRequest request = getRequest(requestId);

        if (request.getStatus() != AuctionRequestStatus.SUBMITTED) {
            throw CustomResponseException.resourceMisMatch(
                    "Only submitted requests can enter review"
            );
        }

        request.setStatus(AuctionRequestStatus.UNDER_REVIEW);
    }

    // Asks the owner to provide corrections before the request can proceed.
    @Transactional
    @Override
    public void requestChanges(
            UUID requestId,
            AuctionRequestReviewDto dto) {

        AuctionRequest request = getRequest(requestId);

        validateUnderReview(request);

        request.setStatus(AuctionRequestStatus.CHANGES_REQUESTED);
        request.setReviewNotes(dto.reviewNotes());
        request.setReviewedAt(Instant.now());
    }

    // Rejects an auction request and records the admin's reason.
    @Transactional
    @Override
    public void rejectRequest(
            UUID requestId,
            AuctionRequestReviewDto dto) {

        AuctionRequest request = getRequest(requestId);

        validateUnderReview(request);

        request.setStatus(AuctionRequestStatus.REJECTED);
        request.setReviewNotes(dto.reviewNotes());
        request.setReviewedAt(Instant.now());
    }

    // Approves a request and stores the reserve price proposed by the admin.
    @Transactional
    @Override
    public void approveRequest(
            UUID requestId,
            AuctionRequestApprovalDto dto) {

        AuctionRequest request = getRequest(requestId);

        validateUnderReview(request);
        validateApproval(request, dto);

        request.setStatus(AuctionRequestStatus.APPROVED);
        request.setProposedReservePrice(dto.proposedReservePrice());
        request.setReviewNotes(dto.reviewNotes());
        request.setReviewedAt(Instant.now());
    }

    // Finds a request or returns 404 when it does not exist.
    private AuctionRequest getRequest(UUID requestId) {

        return requestRepository.findById(requestId)
                .orElseThrow(() ->
                        CustomResponseException.resourceNotFound(
                                "Auction request not found"
                        )
                );
    }

    // Ensures that an admin makes a decision only while reviewing a request.
    private void validateUnderReview(AuctionRequest request) {

        if (request.getStatus() != AuctionRequestStatus.UNDER_REVIEW) {
            throw CustomResponseException.resourceMisMatch(
                    "This request is not currently under review"
            );
        }
    }

    // Checks that the proposed reserve price matches the requested auction type.
    private void validateApproval(
            AuctionRequest request,
            AuctionRequestApprovalDto dto) {

        if (request.getRequestedType() == AuctionType.RESERVE) {

            if (dto.proposedReservePrice() == null
                    || dto.proposedReservePrice().signum() <= 0) {

                throw CustomResponseException.resourceMisMatch(
                        "A valid proposed reserve price is required"
                );
            }

        } else if (request.getRequestedType() == AuctionType.NO_RESERVE) {

            if (dto.proposedReservePrice() != null) {
                throw CustomResponseException.resourceMisMatch(
                        "No-reserve requests cannot have a proposed reserve price"
                );
            }
        }
    }

    // Converts the request entity into the information needed by the admin queue.
    private AdminAuctionRequestResponseDto mapToResponse(
            AuctionRequest request) {

        return new AdminAuctionRequestResponseDto(
                request.getId(),
                request.getCar().getId(),
                request.getCar().getMake(),
                request.getCar().getModel(),
                request.getCar().getOwner().getId(),
                request.getRequestedType(),
                request.getRequestedReservePrice(),
                request.getProposedReservePrice(),
                request.getStatus(),
                request.getSubmittedAt(),
                request.getReviewedAt(),
                request.getReviewNotes()
        );
    }
}

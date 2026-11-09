package auction.auctions.services;

import auction.auctions.abstracts.AuctionService;
import auction.auctions.dtos.AuctionCreateDto;
import auction.auctions.dtos.AuctionResponseDto;
import auction.auctions.entities.Auction;
import auction.auctions.entities.AuctionRequest;
import auction.auctions.enums.AuctionRequestStatus;
import auction.auctions.enums.AuctionResult;
import auction.auctions.enums.AuctionStatus;
import auction.auctions.enums.AuctionType;
import auction.auctions.repositories.AuctionRepository;
import auction.auctions.repositories.AuctionRequestRepository;
import auction.bids.entities.Bid;
import auction.bids.repositories.BidRepository;
import auction.shared.HttpRequests.CustomResponseException;
import auction.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionServiceImp implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionRequestRepository requestRepository;
    private final BidRepository bidRepository;

    // Creates an auction from an approved auction request.
    @Override
    @Transactional
    public AuctionResponseDto createAuction(
            UUID requestId,
            AuctionCreateDto dto,
            User admin
    ) {
        AuctionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> CustomResponseException.resourceNotFound(
                        "Auction request not found."
                ));

        if (request.getStatus() != AuctionRequestStatus.APPROVED) {
            throw CustomResponseException.resourceMisMatch(
                    "Only approved auction requests can be converted into auctions."
            );
        }

        if (auctionRepository.existsByAuctionRequest_Id(requestId)) {
            throw CustomResponseException.resourceDuplicateData(
                    "An auction has already been created for this request."
            );
        }

        validateAuctionSchedule(dto);

        BigDecimal reservePrice = getReservePrice(request);

        Auction auction = new Auction();
        auction.setAuctionRequest(request);
        auction.setAuctionType(request.getRequestedType());
        auction.setStartingPrice(dto.startingPrice());
        auction.setReservePrice(reservePrice);
        auction.setStartsAt(dto.startsAt());
        auction.setEndsAt(dto.endsAt());
        auction.setStatus(AuctionStatus.SCHEDULED);
        auction.setResult(AuctionResult.NOT_DECIDED);
        auction.setCreatedBy(admin);

        Auction savedAuction = auctionRepository.save(auction);

        request.setStatus(AuctionRequestStatus.AUCTION_CREATED);

        return mapToResponse(savedAuction);
    }

    // Starts a scheduled auction when its start time has arrived.
    @Override
    @Transactional
    public AuctionResponseDto startAuction(UUID auctionId) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> CustomResponseException.resourceNotFound(
                        "Auction not found."
                ));

        if (auction.getStatus() != AuctionStatus.SCHEDULED) {
            throw CustomResponseException.resourceMisMatch(
                    "Only scheduled auctions can be started."
            );
        }

        if (Instant.now().isBefore(auction.getStartsAt())) {
            throw CustomResponseException.resourceMisMatch(
                    "Auction start time has not arrived yet."
            );
        }

        if (!Instant.now().isBefore(auction.getEndsAt())) {
            throw CustomResponseException.resourceMisMatch(
                    "Auction has already passed its ending time."
            );
        }

        auction.setStatus(AuctionStatus.LIVE);

        return mapToResponse(auction);
    }

    // Ends a live auction and determines its winner.
    @Override
    @Transactional
    public AuctionResponseDto endAuction(UUID auctionId) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> CustomResponseException.resourceNotFound(
                        "Auction not found."
                ));

        if (auction.getStatus() != AuctionStatus.LIVE) {
            throw CustomResponseException.resourceMisMatch(
                    "Only live auctions can be ended."
            );
        }

        Instant now = Instant.now();

        if (now.isBefore(auction.getEndsAt())) {
            throw CustomResponseException.resourceMisMatch(
                    "Auction ending time has not arrived yet."
            );
        }

        Bid highestBid = bidRepository
                .findTopByAuction_IdOrderByAmountDescPlacedAtDesc(auctionId)
                .orElse(null);

        if (highestBid == null) {
            auction.setResult(AuctionResult.UNSOLD);
        } else {
            auction.setWinner(highestBid.getBidder());
            auction.setFinalPrice(highestBid.getAmount());
            auction.setResult(AuctionResult.SOLD);
        }

        auction.setStatus(AuctionStatus.ENDED);

        return mapToResponse(auction);
    }

    // Cancels an auction that has not started yet.
    @Override
    @Transactional
    public AuctionResponseDto cancelAuction(UUID auctionId) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> CustomResponseException.resourceNotFound(
                        "Auction not found."
                ));

        if (auction.getStatus() != AuctionStatus.SCHEDULED) {
            throw CustomResponseException.resourceMisMatch(
                    "Only scheduled auctions can be cancelled."
            );
        }

        auction.setStatus(AuctionStatus.CANCELLED);

        return mapToResponse(auction);
    }

    // Ensures the auction has a valid start and end time.
    private void validateAuctionSchedule(AuctionCreateDto dto) {
        if (!dto.endsAt().isAfter(dto.startsAt())) {
            throw CustomResponseException.resourceMisMatch(
                    "Auction end time must be after its start time."
            );
        }

        if (dto.startingPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw CustomResponseException.resourceMisMatch(
                    "Starting price must be greater than zero."
            );
        }
    }

    // Gets the final reserve price from the approved request.
    private BigDecimal getReservePrice(AuctionRequest request) {
        if (request.getRequestedType() == AuctionType.NO_RESERVE) {
            return null;
        }

        BigDecimal reservePrice = request.getProposedReservePrice();

        if (reservePrice == null || reservePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw CustomResponseException.resourceMisMatch(
                    "The approved request must have a valid proposed reserve price."
            );
        }

        return reservePrice;
    }

    // Converts an auction entity into its public response DTO.
    private AuctionResponseDto mapToResponse(Auction auction) {
        return new AuctionResponseDto(
                auction.getId(),
                auction.getAuctionRequest().getCar().getId(),
                auction.getAuctionRequest().getCar().getMake(),
                auction.getAuctionRequest().getCar().getModel(),
                auction.getAuctionType(),
                auction.getStartingPrice(),
                auction.getMinimumBidIncrement(),
                auction.getStartsAt(),
                auction.getEndsAt(),
                auction.getStatus(),
                auction.getResult(),
                auction.getWinner() == null ? null : auction.getWinner().getId(),
                auction.getFinalPrice()
        );
    }
}
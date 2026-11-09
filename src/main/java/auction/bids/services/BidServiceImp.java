package auction.bids.services;

import auction.auctions.entities.Auction;
import auction.auctions.enums.AuctionStatus;
import auction.auctions.repositories.AuctionRepository;
import auction.bids.abstracts.BidService;
import auction.bids.dtos.BidCreateDto;
import auction.bids.dtos.BidResponseDto;
import auction.bids.entities.Bid;
import auction.bids.repositories.BidRepository;
import auction.shared.HttpRequests.CustomResponseException;
import auction.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidServiceImp implements BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    // Places a bid on a live auction.
    @Override
    @Transactional
    public BidResponseDto placeBid(
            UUID auctionId,
            BidCreateDto dto,
            User bidder
    ) {

        // Retrieve and lock the auction to prevent simultaneous bid conflicts.
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> CustomResponseException.resourceNotFound(
                        "Auction not found."
                ));

        // Ensure the auction is currently live.
        if (auction.getStatus() != AuctionStatus.LIVE) {
            throw CustomResponseException.resourceMisMatch(
                    "Bids can only be placed on live auctions."
            );
        }

        // Ensure the auction has not reached its ending time.
        if (!Instant.now().isBefore(auction.getEndsAt())) {
            throw CustomResponseException.resourceMisMatch(
                    "This auction has already ended."
            );
        }

        // Prevent the car owner from bidding on their own car.
        UUID ownerId = auction.getAuctionRequest()
                .getCar()
                .getOwner()
                .getId();

        if (ownerId.equals(bidder.getId())) {
            throw CustomResponseException.forbidden(
                    "You cannot bid on your own car."
            );
        }

        // Retrieve the current highest bid.
        Bid highestBid = bidRepository
                .findTopByAuction_IdOrderByAmountDescPlacedAtDesc(auctionId)
                .orElse(null);

        // Calculate the minimum acceptable bid.
        BigDecimal minimumBid = calculateMinimumBid(auction, highestBid);

        // Reject bids that do not meet the minimum amount.
        if (dto.amount().compareTo(minimumBid) < 0) {
            throw CustomResponseException.resourceMisMatch(
                    "Your bid must be at least " + minimumBid + "."
            );
        }

        // Create and save the new bid.
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setAmount(dto.amount());

        Bid savedBid = bidRepository.save(bid);

        return mapToResponse(savedBid);
    }

    // Retrieves all bids for an auction, ordered by highest amount first.
    @Override
    @Transactional(readOnly = true)
    public List<BidResponseDto> getAuctionBids(UUID auctionId) {

        if (!auctionRepository.existsById(auctionId)) {
            throw CustomResponseException.resourceNotFound(
                    "Auction not found."
            );
        }

        return bidRepository.findByAuction_IdOrderByAmountDesc(auctionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Retrieves the current highest bid for an auction.
    @Override
    @Transactional(readOnly = true)
    public BidResponseDto getHighestBid(UUID auctionId) {

        if (!auctionRepository.existsById(auctionId)) {
            throw CustomResponseException.resourceNotFound(
                    "Auction not found."
            );
        }

        return bidRepository
                .findTopByAuction_IdOrderByAmountDescPlacedAtDesc(auctionId)
                .map(this::mapToResponse)
                .orElse(null);
    }

    // Calculates the minimum acceptable bid based on the current highest bid.
    private BigDecimal calculateMinimumBid(Auction auction, Bid highestBid) {

        if (highestBid == null) {
            return auction.getStartingPrice();
        }

        return highestBid.getAmount()
                .add(auction.getMinimumBidIncrement());
    }

    // Converts a bid entity into its response DTO.
    private BidResponseDto mapToResponse(Bid bid) {
        return new BidResponseDto(
                bid.getId(),
                bid.getAuction().getId(),
                bid.getBidder().getId(),
                bid.getAmount(),
                bid.getPlacedAt()
        );
    }
}
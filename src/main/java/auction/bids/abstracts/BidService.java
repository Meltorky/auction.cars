package auction.bids.abstracts;

import auction.bids.dtos.BidCreateDto;
import auction.bids.dtos.BidResponseDto;
import auction.users.User;

import java.util.List;
import java.util.UUID;

public interface BidService {

    // Places a bid on a live auction.
    BidResponseDto placeBid(UUID auctionId, BidCreateDto dto, User bidder);

    // Retrieves all bids for an auction.
    List<BidResponseDto> getAuctionBids(UUID auctionId);

    // Retrieves the current highest bid for an auction.
    BidResponseDto getHighestBid(UUID auctionId);
}
package auction.auctions.abstracts;

import auction.auctions.dtos.AuctionCreateDto;
import auction.auctions.dtos.AuctionResponseDto;
import auction.users.User;

import java.util.UUID;

public interface AuctionService {

    // Creates an auction from an approved auction request.
    AuctionResponseDto createAuction(
            UUID requestId,
            AuctionCreateDto dto,
            User admin
    );
}
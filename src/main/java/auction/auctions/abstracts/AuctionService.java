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

    // Starts a scheduled auction when its start time has arrived.
    AuctionResponseDto startAuction(UUID auctionId);

    // Ends a live auction and determines its winner.
    AuctionResponseDto endAuction(UUID auctionId);

    // Cancels an auction that has not started yet.
    AuctionResponseDto cancelAuction(UUID auctionId);
}
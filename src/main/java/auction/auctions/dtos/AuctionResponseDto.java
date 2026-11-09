package auction.auctions.dtos;

import auction.auctions.enums.AuctionResult;
import auction.auctions.enums.AuctionStatus;
import auction.auctions.enums.AuctionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionResponseDto(

        UUID id,

        UUID carId,
        String carMake,
        String carModel,

        AuctionType auctionType,

        BigDecimal startingPrice,
        BigDecimal minimumBidIncrement,

        Instant startsAt,
        Instant endsAt,

        AuctionStatus status,
        AuctionResult result,

        UUID winnerId,
        BigDecimal finalPrice

) {
}

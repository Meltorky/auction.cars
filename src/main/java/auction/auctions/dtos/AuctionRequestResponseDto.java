package auction.auctions.dtos;


import auction.auctions.enums.AuctionType;
import auction.auctions.enums.AuctionRequestStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionRequestResponseDto(

        UUID id,

        UUID carId,

        AuctionType requestedType,

        BigDecimal requestedReservePrice,

        BigDecimal proposedReservePrice,

        AuctionRequestStatus status,

        Instant submittedAt,

        Instant reviewedAt,

        String reviewNotes

) {
}

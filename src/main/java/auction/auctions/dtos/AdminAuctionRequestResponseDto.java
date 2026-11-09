
package auction.auctions.dtos;

import auction.auctions.enums.AuctionRequestStatus;
import auction.auctions.enums.AuctionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminAuctionRequestResponseDto(

        UUID id,

        UUID carId,
        String carMake,
        String carModel,

        UUID ownerId,

        AuctionType requestedType,
        BigDecimal requestedReservePrice,
        BigDecimal proposedReservePrice,

        AuctionRequestStatus status,

        Instant submittedAt,
        Instant reviewedAt,

        String reviewNotes

) {
}

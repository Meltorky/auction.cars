
package auction.auctions.dtos;

import auction.auctions.enums.AuctionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record AuctionRequestUpdateDto(

        @NotNull(message = "Auction type is required")
        AuctionType requestedType,

        @DecimalMin(
                value = "0.01",
                message = "Reserve price must be greater than zero"
        )
        BigDecimal requestedReservePrice

) {
}

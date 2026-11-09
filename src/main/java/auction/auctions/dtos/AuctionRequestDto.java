package auction.auctions.dtos;

import auction.auctions.enums.AuctionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionRequestDto(

        @NotNull(message = "Car ID is required")
        UUID carId,

        @NotNull(message = "Auction type is required")
        AuctionType requestedType,

        @DecimalMin(
                value = "0.01",
                message = "Reserve price must be greater than zero"
        )
        BigDecimal requestedReservePrice

) {
}

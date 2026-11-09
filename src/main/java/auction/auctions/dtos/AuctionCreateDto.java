
package auction.auctions.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.time.Instant;

public record AuctionCreateDto(

        @NotNull(message = "Starting price is required")
        @DecimalMin(
                value = "0.01",
                message = "Starting price must be greater than zero"
        )
        BigDecimal startingPrice,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        Instant startsAt,

        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        Instant endsAt

) {
}

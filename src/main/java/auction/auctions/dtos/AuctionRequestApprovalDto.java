
package auction.auctions.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AuctionRequestApprovalDto(

        @DecimalMin(
                value = "0.01",
                message = "Proposed reserve price must be greater than zero"
        )
        BigDecimal proposedReservePrice,

        @Size(max = 1000, message = "Review notes cannot exceed 1000 characters")
        String reviewNotes

) {
}

package auction.auctions.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuctionRequestReviewDto(

        @NotBlank(message = "Review notes are required")
        @Size(max = 1000, message = "Review notes cannot exceed 1000 characters")
        String reviewNotes

) {
}

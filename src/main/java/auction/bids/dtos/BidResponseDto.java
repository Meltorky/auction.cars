package auction.bids.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BidResponseDto(
        UUID id,
        UUID auctionId,
        UUID bidderId,
        BigDecimal amount,
        Instant placedAt
) {}
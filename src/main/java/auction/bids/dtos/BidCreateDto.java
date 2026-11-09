package auction.bids.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BidCreateDto(

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount

) {}
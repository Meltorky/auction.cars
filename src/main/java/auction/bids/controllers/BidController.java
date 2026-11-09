package auction.bids.controllers;

import auction.bids.dtos.BidCreateDto;
import auction.bids.dtos.BidResponseDto;
import auction.bids.abstracts.BidService;
import auction.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auctions/{auctionId}/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    // Places a bid on an auction.
    @PostMapping
    public BidResponseDto placeBid(
            @PathVariable UUID auctionId,
            @Valid @RequestBody BidCreateDto dto,
            @RequestAttribute("currentUser") User currentUser
    ) {
        return bidService.placeBid(auctionId, dto, currentUser);
    }

    // Retrieves all bids for an auction.
    @GetMapping
    public List<BidResponseDto> getAuctionBids(
            @PathVariable UUID auctionId
    ) {
        return bidService.getAuctionBids(auctionId);
    }

    // Retrieves the current highest bid for an auction.
    @GetMapping("/highest")
    public BidResponseDto getHighestBid(
            @PathVariable UUID auctionId
    ) {
        return bidService.getHighestBid(auctionId);
    }
}
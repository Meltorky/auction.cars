package auction.auctions.controllers;

import auction.auctions.dtos.AuctionCreateDto;
import auction.auctions.dtos.AuctionResponseDto;
import auction.auctions.abstracts.AuctionService;
import auction.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    // Creates an auction from an approved auction request.
    @PostMapping("/{requestId}")
    public AuctionResponseDto createAuction(
            @PathVariable UUID requestId,
            @Valid @RequestBody AuctionCreateDto dto,
            @RequestAttribute("currentUser") User currentUser
    ) {
        return auctionService.createAuction(requestId, dto, currentUser);
    }

    // Starts a scheduled auction.
    @PatchMapping("/{auctionId}/start")
    public AuctionResponseDto startAuction(
            @PathVariable UUID auctionId
    ) {
        return auctionService.startAuction(auctionId);
    }

    // Ends a live auction and determines its winner.
    @PatchMapping("/{auctionId}/end")
    public AuctionResponseDto endAuction(
            @PathVariable UUID auctionId
    ) {
        return auctionService.endAuction(auctionId);
    }

    // Cancels a scheduled auction.
    @PatchMapping("/{auctionId}/cancel")
    public AuctionResponseDto cancelAuction(
            @PathVariable UUID auctionId
    ) {
        return auctionService.cancelAuction(auctionId);
    }
}
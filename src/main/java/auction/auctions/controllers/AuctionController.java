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
}

package auction.auctions.controllers;

import auction.auctions.dtos.AuctionRequestDto;
import auction.auctions.dtos.AuctionRequestResponseDto;
import auction.auctions.dtos.AuctionRequestUpdateDto;
import auction.auctions.services.AuctionRequestServiceImp;
import auction.cars.abstracts.AuctionRequestService;
import auction.users.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auction-requests")
@RequiredArgsConstructor
public class AuctionRequestController {

    private final AuctionRequestService requestService;

    @PostMapping
    public ResponseEntity<AuctionRequestResponseDto> submitRequest(
            @Valid @RequestBody AuctionRequestDto dto,
            @RequestAttribute("currentUser") User currentUser) {

        AuctionRequestResponseDto response =
                requestService.submitRequest(dto, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AuctionRequestResponseDto>> getMyRequests(
            @RequestAttribute("currentUser") User currentUser) {

        return ResponseEntity.ok(
                requestService.getMyRequests(currentUser.getId())
        );
    }

    @PatchMapping("/{requestId}/withdraw")
    public ResponseEntity<Void> withdrawRequest(
            @PathVariable UUID requestId,
            @RequestAttribute("currentUser") User currentUser) {

        requestService.withdrawRequest(requestId, currentUser);

        return ResponseEntity.noContent().build();
    }


    // Updates a request using admin feedback and sends it back for review.
    @PutMapping("/{requestId}/resubmit")
    public ResponseEntity<AuctionRequestResponseDto> updateAndResubmit(
            @PathVariable UUID requestId,
            @Valid @RequestBody AuctionRequestUpdateDto dto,
            @RequestAttribute("currentUser") User currentUser) {

        return ResponseEntity.ok(
                requestService.updateAndResubmit(
                        requestId,
                        dto,
                        currentUser
                )
        );
    }

}

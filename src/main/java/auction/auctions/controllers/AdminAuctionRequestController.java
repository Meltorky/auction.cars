
package auction.auctions.controllers;

import auction.auctions.dtos.AdminAuctionRequestResponseDto;
import auction.auctions.dtos.AuctionRequestApprovalDto;
import auction.auctions.dtos.AuctionRequestReviewDto;
import auction.auctions.services.AdminAuctionRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/auction-requests")
@RequiredArgsConstructor
public class AdminAuctionRequestController {

    private final AdminAuctionRequestService adminService;

    // Retrieves all requests waiting for admin review.
    @GetMapping("/pending")
    public ResponseEntity<List<AdminAuctionRequestResponseDto>> getPendingRequests() {

        return ResponseEntity.ok(adminService.getPendingRequests());
    }

    // Starts reviewing a submitted auction request.
    @PatchMapping("/{requestId}/start-review")
    public ResponseEntity<Void> startReview(
            @PathVariable UUID requestId) {

        adminService.startReview(requestId);

        return ResponseEntity.noContent().build();
    }

    // Sends the request back to its owner for corrections.
    @PatchMapping("/{requestId}/request-changes")
    public ResponseEntity<Void> requestChanges(
            @PathVariable UUID requestId,
            @Valid @RequestBody AuctionRequestReviewDto dto) {

        adminService.requestChanges(requestId, dto);

        return ResponseEntity.noContent().build();
    }

    // Rejects an auction request with an explanation.
    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody AuctionRequestReviewDto dto) {

        adminService.rejectRequest(requestId, dto);

        return ResponseEntity.noContent().build();
    }

    // Approves an auction request with the admin's proposed reserve price.
    @PatchMapping("/{requestId}/approve")
    public ResponseEntity<Void> approveRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody AuctionRequestApprovalDto dto) {

        adminService.approveRequest(requestId, dto);

        return ResponseEntity.noContent().build();
    }
}

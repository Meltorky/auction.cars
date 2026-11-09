
package auction.auctions.services;

import auction.auctions.dtos.AuctionRequestDto;
import auction.auctions.dtos.AuctionRequestResponseDto;
import auction.auctions.dtos.AuctionRequestUpdateDto;
import auction.auctions.entities.AuctionRequest;
import auction.auctions.enums.AuctionRequestStatus;
import auction.auctions.repositories.AuctionRequestRepository;
import auction.auctions.enums.AuctionStatus;
import auction.auctions.enums.AuctionType;
import auction.auctions.repositories.AuctionRepository;
import auction.cars.entities.Car;
import auction.cars.enums.CarReviewStatus;
import auction.cars.repositories.CarRepository;
import auction.shared.HttpRequests.CustomResponseException;
import auction.users.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionRequestService {

    private final AuctionRequestRepository requestRepository;
    private final CarRepository carRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public AuctionRequestResponseDto submitRequest(
            AuctionRequestDto dto,
            User owner) {

        Car car = carRepository.findById(dto.carId())
                .orElseThrow(() ->
                        CustomResponseException.resourceNotFound(
                                "Car not found"
                        )
                );

        validateOwner(car, owner);
        validateCar(car);
        validateRequest(
                dto.requestedType(),
                dto.requestedReservePrice()
        );        validateNoActiveRequest(car.getId());

        AuctionRequest request = new AuctionRequest();

        request.setCar(car);
        request.setRequestedType(dto.requestedType());
        request.setRequestedReservePrice(dto.requestedReservePrice());
        request.setStatus(AuctionRequestStatus.SUBMITTED);
        request.setSubmittedAt(Instant.now());

        AuctionRequest savedRequest = requestRepository.save(request);

        return mapToResponse(savedRequest);
    }

    @Transactional(readOnly = true)
    public List<AuctionRequestResponseDto> getMyRequests(UUID ownerId) {

        return requestRepository
                .findByCar_Owner_IdOrderBySubmittedAtDesc(ownerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void withdrawRequest(UUID requestId, User owner) {

        AuctionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() ->
                        CustomResponseException.resourceNotFound(
                                "Auction request not found"
                        )
                );

        validateOwner(request.getCar(), owner);

        AuctionRequestStatus status = request.getStatus();

        if (status != AuctionRequestStatus.SUBMITTED
                && status != AuctionRequestStatus.UNDER_REVIEW
                && status != AuctionRequestStatus.CHANGES_REQUESTED) {

            throw CustomResponseException.resourceMisMatch(
                    "This auction request can no longer be withdrawn"
            );
        }

        request.setStatus(AuctionRequestStatus.WITHDRAWN);
    }


    // Updates a request after admin feedback and resubmits it for review.
    @Transactional
    public AuctionRequestResponseDto updateAndResubmit(
            UUID requestId,
            AuctionRequestUpdateDto dto,
            User owner) {

        AuctionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() ->
                        CustomResponseException.resourceNotFound(
                                "Auction request not found"
                        )
                );

        validateOwner(request.getCar(), owner);

        if (request.getStatus() != AuctionRequestStatus.CHANGES_REQUESTED) {
            throw CustomResponseException.resourceMisMatch(
                    "Only requests with requested changes can be updated"
            );
        }

        validateRequest(dto.requestedType(), dto.requestedReservePrice());

        request.setRequestedType(dto.requestedType());
        request.setRequestedReservePrice(dto.requestedReservePrice());

        request.setStatus(AuctionRequestStatus.SUBMITTED);
        request.setSubmittedAt(Instant.now());
        request.setReviewedAt(null);
        request.setReviewNotes(null);

        return mapToResponse(request);
    }


    private void validateOwner(Car car, User owner) {

        if (!car.getOwner().getId().equals(owner.getId())) {
            throw CustomResponseException.forbidden(
                    "You are not allowed to access this car"
            );
        }
    }

    private void validateCar(Car car) {

        if (car.getReviewStatus() != CarReviewStatus.APPROVED) {
            throw CustomResponseException.resourceMisMatch(
                    "Only approved cars can be submitted for auction"
            );
        }

        boolean hasActiveAuction =
                auctionRepository.existsByAuctionRequest_Car_IdAndStatusIn(
                        car.getId(),
                        List.of(
                                AuctionStatus.SCHEDULED,
                                AuctionStatus.LIVE
                        )
                );

        if (hasActiveAuction) {
            throw CustomResponseException.resourceMisMatch(
                    "This car already has an active auction"
            );
        }
    }


    // Validates the reserve price according to the selected auction type.
    private void validateRequest(
            AuctionType requestedType,
            BigDecimal requestedReservePrice) {

        if (requestedType == AuctionType.RESERVE) {

            if (requestedReservePrice == null
                    || requestedReservePrice.signum() <= 0) {

                throw CustomResponseException.resourceMisMatch(
                        "A valid reserve price is required for reserve auctions"
                );
            }

        } else if (requestedType == AuctionType.NO_RESERVE) {

            if (requestedReservePrice != null) {
                throw CustomResponseException.resourceMisMatch(
                        "No-reserve auctions cannot have a reserve price"
                );
            }
        }
    }


    private void validateNoActiveRequest(UUID carId) {

        List<AuctionRequestStatus> activeStatuses = List.of(
                AuctionRequestStatus.SUBMITTED,
                AuctionRequestStatus.UNDER_REVIEW,
                AuctionRequestStatus.CHANGES_REQUESTED,
                AuctionRequestStatus.APPROVED
        );

        if (requestRepository.existsByCar_IdAndStatusIn(
                carId,
                activeStatuses
        )) {
            throw CustomResponseException.resourceMisMatch(
                    "This car already has an active auction request"
            );
        }
    }

    private AuctionRequestResponseDto mapToResponse(
            AuctionRequest request) {

        return new AuctionRequestResponseDto(
                request.getId(),
                request.getCar().getId(),
                request.getRequestedType(),
                request.getRequestedReservePrice(),
                request.getProposedReservePrice(),
                request.getStatus(),
                request.getSubmittedAt(),
                request.getReviewedAt(),
                request.getReviewNotes()
        );
    }
}

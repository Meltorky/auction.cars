
package auction.cars.services;

import auction.auctions.enums.AuctionStatus;
import auction.auctions.repositories.AuctionRepository;
import auction.auctions.repositories.AuctionRequestRepository;
import auction.HttpRequests.CustomResponseException;
import auction.cars.dtos.CarRequestDto;
import auction.cars.dtos.CarResponseDto;
import auction.cars.entities.Car;
import auction.cars.enums.CarReviewStatus;
import auction.cars.repositories.CarRepository;
import auction.users.User;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarService implements auction.cars.abstracts.CarService {

    private final CarRepository carRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionRequestRepository requestRepository;

    // Create a new car
    @Transactional
    @Override
    public CarResponseDto createCar(CarRequestDto dto, User owner) {

        Car car = new Car();

        car.setOwner(owner);
        applyChanges(car, dto);

        car.setReviewStatus(CarReviewStatus.DRAFT);

        Car savedCar = carRepository.save(car);

        return mapToResponse(savedCar);
    }

    // Update an existing car
    @Transactional
    @Override
    public CarResponseDto updateCar(
            UUID carId,
            CarRequestDto dto,
            User owner
    ) {

        Car car = getCar(carId);

        validateOwner(car, owner);
        validateCanEdit(car);

        applyChanges(car, dto);

        // Any edit requires a new admin review
        car.setReviewStatus(CarReviewStatus.DRAFT);
        car.setSubmittedAt(null);
        car.setReviewedAt(null);
        car.setReviewNotes(null);

        return mapToResponse(car);
    }

    // Submit car for admin review
    @Transactional
    @Override
    public CarResponseDto submitForReview(UUID carId, User owner) {

        Car car = getCar(carId);

        validateOwner(car, owner);

        if (car.getReviewStatus() != CarReviewStatus.DRAFT) {
            throw CustomResponseException.resourceMisMatch(
                    "Only draft cars can be submitted for review"
            );
        }

        car.setReviewStatus(CarReviewStatus.SUBMITTED);
        car.setSubmittedAt(Instant.now());

        return mapToResponse(car);
    }

    // Delete a car before its first review submission
    @Transactional
    @Override
    public void deleteCar(UUID carId, User owner) {

        Car car = getCar(carId);

        validateOwner(car, owner);

        if (car.getReviewStatus() != CarReviewStatus.DRAFT) {
            throw CustomResponseException.resourceMisMatch(
                    "Only draft cars can be deleted"
            );
        }

        if (requestRepository.existsByCar_Id(carId)) {
            throw CustomResponseException.resourceMisMatch(
                    "This car has auction request history and cannot be deleted"
            );
        }

        carRepository.delete(car);
    }

    // Get all cars belonging to the current owner
    @Transactional(readOnly = true)
    @Override
    public List<CarResponseDto> getMyCars(UUID ownerId) {

        return carRepository.findByOwner_Id(ownerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Helper methods

    private Car getCar(UUID carId) {

        return carRepository.findById(carId)
                .orElseThrow(() ->
                        CustomResponseException.resourceNotFound(
                                "Car not found"
                        )
                );
    }

    private void validateOwner(Car car, User owner) {

        if (!car.getOwner().getId().equals(owner.getId())) {
            throw CustomResponseException.forbidden(
                    "You are not the owner of this car"
            );
        }
    }

    private void validateCanEdit(Car car) {

        CarReviewStatus status = car.getReviewStatus();

        if (status == CarReviewStatus.SUBMITTED ||
                status == CarReviewStatus.UNDER_REVIEW) {

            throw CustomResponseException.resourceMisMatch(
                    "You cannot edit a car while it is being reviewed"
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
                    "You cannot edit a car with a scheduled or live auction"
            );
        }
    }

    private void applyChanges(Car car, CarRequestDto dto) {

        car.setMake(dto.make());
        car.setModel(dto.model());
        car.setMileage(dto.mileage());
        car.setEngine(dto.engine());
        car.setBodyStyle(dto.bodyStyle());
        car.setExteriorColor(dto.exteriorColor());
        car.setInteriorColor(dto.interiorColor());
        car.setDescription(dto.description());
    }

    private CarResponseDto mapToResponse(Car car) {

        return new CarResponseDto(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getMileage(),
                car.getEngine(),
                car.getBodyStyle(),
                car.getExteriorColor(),
                car.getInteriorColor(),
                car.getDescription(),
                car.getOwner().getId(),
                car.getReviewStatus(),
                car.getSubmittedAt(),
                car.getReviewedAt(),
                car.getReviewNotes()
        );
    }
}

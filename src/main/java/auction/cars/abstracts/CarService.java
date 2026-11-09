package auction.cars.abstracts;

import auction.cars.dtos.CarRequestDto;
import auction.cars.dtos.CarResponseDto;
import auction.users.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CarService {
    // Create a new car
    @Transactional
    CarResponseDto createCar(CarRequestDto dto, User owner);

    // Update an existing car
    @Transactional
    CarResponseDto updateCar(
            UUID carId,
            CarRequestDto dto,
            User owner
    );

    // Submit car for admin review
    @Transactional
    CarResponseDto submitForReview(UUID carId, User owner);

    // Delete a car before its first review submission
    @Transactional
    void deleteCar(UUID carId, User owner);

    // Get all cars belonging to the current owner
    @Transactional(readOnly = true)
    List<CarResponseDto> getMyCars(UUID ownerId);
}


package auction.cars.controllers;

import auction.cars.dtos.CarRequestDto;
import auction.cars.dtos.CarResponseDto;
import auction.cars.services.CarService;
import auction.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarResponseDto> createCar(
            @Valid @RequestBody CarRequestDto dto,
            @RequestAttribute("currentUser") User currentUser) {

        CarResponseDto response = carService.createCar(dto, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<CarResponseDto>> getMyCars(
            @RequestAttribute("currentUser") User currentUser) {

        return ResponseEntity.ok(
                carService.getMyCars(currentUser.getId())
        );
    }

    @PutMapping("/{carId}")
    public ResponseEntity<CarResponseDto> updateCar(
            @PathVariable UUID carId,
            @Valid @RequestBody CarRequestDto dto,
            @RequestAttribute("currentUser") User currentUser) {

        return ResponseEntity.ok(
                carService.updateCar(carId, dto, currentUser)
        );
    }

    @PatchMapping("/{carId}/submit")
    public ResponseEntity<CarResponseDto> submitForReview(
            @PathVariable UUID carId,
            @RequestAttribute("currentUser") User currentUser) {

        return ResponseEntity.ok(
                carService.submitForReview(carId, currentUser)
        );
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> deleteCar(
            @PathVariable UUID carId,
            @RequestAttribute("currentUser") User currentUser) {

        carService.deleteCar(carId, currentUser);

        return ResponseEntity.noContent().build();
    }
}

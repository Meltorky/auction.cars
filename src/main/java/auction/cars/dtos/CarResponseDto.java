
package auction.cars.dtos;

import auction.cars.enums.BodyStyle;
import auction.cars.enums.CarReviewStatus;

import java.time.Instant;
import java.util.UUID;

public record CarResponseDto(

        UUID id,
        String make,
        String model,
        int mileage,
        String engine,
        BodyStyle bodyStyle,
        String exteriorColor,
        String interiorColor,
        String description,
        UUID ownerId,
        CarReviewStatus reviewStatus,
        Instant submittedAt,
        Instant reviewedAt,
        String reviewNotes

) {}

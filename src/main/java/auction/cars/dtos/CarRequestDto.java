
package auction.cars.dtos;

import auction.cars.enums.BodyStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CarRequestDto(

        @NotBlank(message = "Make is required")
        @Size(max = 100)
        String make,

        @NotBlank(message = "Model is required")
        @Size(max = 100)
        String model,

        @PositiveOrZero(message = "Mileage cannot be negative")
        int mileage,

        @Size(max = 100)
        String engine,

        @NotNull(message = "Body style is required")
        BodyStyle bodyStyle,

        @Size(max = 50)
        String exteriorColor,

        @Size(max = 50)
        String interiorColor,

        String description

) {}

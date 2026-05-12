package com.paddlecourt.booking.dto;

import com.paddlecourt.booking.model.Court;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtDto {

    private Long id;

    @NotBlank(message = "Court name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @NotNull(message = "Court type is required")
    private Court.CourtType type;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal pricePerHour;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private boolean available = true;
}

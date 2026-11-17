
package com.assignment.dataset_operator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response returned by the API")
public class ErrorResponse {

    @Schema(
            description = "Date and time when the error occurred",
            example = "2026-11-17T10:30:00"
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "HTTP status code",
            example = "404"
    )
    private int status;

    @Schema(
            description = "HTTP error description",
            example = "Not Found"
    )
    private String error;

    @Schema(
            description = "Detailed error message",
            example = "No records found for dataset: unknown_dataset"
    )
    private String message;
}

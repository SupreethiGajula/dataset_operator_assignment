
package com.assignment.dataset_operator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after successfully inserting a JSON record")
public class InsertRecordResponse {

    @Schema(
            description = "Confirmation message",
            example = "Record added successfully"
    )
    private String message;

    @Schema(
            description = "Name of the dataset where the record was stored",
            example = "employee_dataset"
    )
    private String dataset;

    @Schema(
            description = "Unique ID assigned to the inserted record",
            example = "1"
    )
    private Long recordId;
}

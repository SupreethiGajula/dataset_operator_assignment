
package com.assignment.dataset_operator.controller;

import com.assignment.dataset_operator.dto.InsertRecordResponse;
import com.assignment.dataset_operator.dto.QueryResponse;
import com.assignment.dataset_operator.service.DatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dataset")
@RequiredArgsConstructor
@Tag(
        name = "Dataset Operations",
        description = "APIs for inserting, retrieving, grouping and sorting JSON dataset records"
)
public class DatasetController {

    private final DatasetService datasetService;

    @PostMapping("/{datasetName}/record")
    @Operation(
            summary = "Insert a JSON record",
            description = "Stores an arbitrary JSON record in the specified dataset."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Record created successfully",
                    content = @Content(schema = @Schema(implementation = InsertRecordResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid or empty JSON request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InsertRecordResponse> insertRecord(

            @Parameter(description = "Name of the dataset", example = "employee_dataset")
            @PathVariable String datasetName,

            @org.springframework.web.bind.annotation.RequestBody
            @RequestBody(
                    description = "JSON record to be inserted",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}"
                            )
                    )
            )
            Map<String, Object> recordPayload) {

        InsertRecordResponse response =
                datasetService.insertRecord(datasetName, recordPayload);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{datasetName}/query")
    @Operation(
            summary = "Query dataset records",
            description = """
                    Retrieves all records, groups records by a specified JSON field,
                    or sorts records by a specified JSON field.

                    Provide groupBy OR sortBy. If neither is supplied,
                    all records are returned.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dataset records retrieved successfully",
                    content = @Content(schema = @Schema(implementation = QueryResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter or sort order"),
            @ApiResponse(responseCode = "404", description = "Dataset not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<QueryResponse> queryDataset(

            @Parameter(description = "Name of the dataset", example = "employee_dataset")
            @PathVariable String datasetName,

            @Parameter(description = "JSON field to group records by", example = "department")
            @RequestParam(required = false) String groupBy,

            @Parameter(description = "JSON field to sort records by", example = "age")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Sort order: asc or desc", example = "asc")
            @RequestParam(required = false, defaultValue = "asc") String order) {

        QueryResponse response =
                datasetService.queryDataset(datasetName, groupBy, sortBy, order);

        return ResponseEntity.ok(response);
    }
}

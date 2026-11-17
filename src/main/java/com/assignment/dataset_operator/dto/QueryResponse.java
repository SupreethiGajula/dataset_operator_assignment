
package com.assignment.dataset_operator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response containing dataset records in grouped, sorted, or normal format")
public class QueryResponse {

    @Schema(
            description = "Records grouped by the requested JSON field. The key represents the field value.",
            example = """
                    {
                      "Engineering": [
                        {
                          "id": 1,
                          "name": "John Doe",
                          "age": 30,
                          "department": "Engineering"
                        }
                      ],
                      "Marketing": [
                        {
                          "id": 2,
                          "name": "Alice Brown",
                          "age": 28,
                          "department": "Marketing"
                        }
                      ]
                    }
                    """
    )
    private Map<String, List<Map<String, Object>>> groupedRecords;

    @Schema(
            description = "Records sorted by the requested JSON field",
            example = """
                    [
                      {
                        "id": 2,
                        "name": "Jane Smith",
                        "age": 25,
                        "department": "Engineering"
                      },
                      {
                        "id": 1,
                        "name": "John Doe",
                        "age": 30,
                        "department": "Engineering"
                      }
                    ]
                    """
    )
    private List<Map<String, Object>> sortedRecords;

    @Schema(
            description = "All records in the dataset when no grouping or sorting parameter is provided",
            example = """
                    [
                      {
                        "id": 1,
                        "name": "John Doe",
                        "age": 30,
                        "department": "Engineering"
                      }
                    ]
                    """
    )
    private List<Map<String, Object>> records;
}

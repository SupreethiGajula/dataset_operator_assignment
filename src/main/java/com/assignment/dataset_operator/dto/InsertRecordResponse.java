package com.assignment.dataset_operator.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsertRecordResponse {
    private String message;
    private String dataset;
    private Long recordId;
}
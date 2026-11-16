package com.assignment.dataset_operator.controller;

import com.assignment.dataset_operator.dto.InsertRecordResponse;
import com.assignment.dataset_operator.dto.QueryResponse;
import com.assignment.dataset_operator.service.DatasetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dataset")
@RequiredArgsConstructor
public class DatasetController {

    private final DatasetService datasetService;

    @PostMapping("/{datasetName}/record")
    public ResponseEntity<InsertRecordResponse> insertRecord(
            @PathVariable String datasetName,
            @RequestBody Map<String, Object> recordPayload) {

        InsertRecordResponse response = datasetService.insertRecord(datasetName, recordPayload);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{datasetName}/query")
    public ResponseEntity<QueryResponse> queryDataset(
            @PathVariable String datasetName,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order) {

        QueryResponse response = datasetService.queryDataset(datasetName, groupBy, sortBy, order);
        return ResponseEntity.ok(response);
    }
}
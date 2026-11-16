
package com.assignment.dataset_operator.service;

import com.assignment.dataset_operator.dto.InsertRecordResponse;
import com.assignment.dataset_operator.dto.QueryResponse;
import com.assignment.dataset_operator.entity.DatasetRecordEntity;
import com.assignment.dataset_operator.exception.DatasetNotFoundException;
import com.assignment.dataset_operator.exception.InvalidQueryParameterException;
import com.assignment.dataset_operator.repository.DatasetRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatasetServiceImpl implements DatasetService {

    private final DatasetRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public InsertRecordResponse insertRecord(String datasetName, Map<String, Object> recordData) {

        if (recordData == null || recordData.isEmpty()) {
            throw new InvalidQueryParameterException("Record payload cannot be empty");
        }

        try {
            String jsonString = objectMapper.writeValueAsString(recordData);

            DatasetRecordEntity entity = DatasetRecordEntity.builder()
                    .datasetName(datasetName)
                    .payloadJson(jsonString)
                    .build();

            DatasetRecordEntity savedEntity = repository.save(entity);

            return InsertRecordResponse.builder()
                    .message("Record added successfully")
                    .dataset(datasetName)
                    .recordId(savedEntity.getId())
                    .build();

        } catch (JacksonException e) {
            throw new InvalidQueryParameterException("Invalid JSON format in record payload");
        }
    }

    @Override
    public QueryResponse queryDataset(String datasetName, String groupBy, String sortBy, String order) {

        List<DatasetRecordEntity> entities = repository.findByDatasetName(datasetName);

        if (entities.isEmpty()) {
            throw new DatasetNotFoundException("No records found for dataset: " + datasetName);
        }

        List<Map<String, Object>> records = entities.stream()
                .map(this::deserializeJson)
                .collect(Collectors.toList());

        // 1. Handle Group-By operation
        if (groupBy != null && !groupBy.isBlank()) {

            Map<String, List<Map<String, Object>>> grouped = records.stream()
                    .collect(Collectors.groupingBy(
                            record -> String.valueOf(record.getOrDefault(groupBy, "UNSPECIFIED")),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            return QueryResponse.builder()
                    .groupedRecords(grouped)
                    .build();
        }

        // 2. Handle Sort-By operation
        if (sortBy != null && !sortBy.isBlank()) {

            if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
                throw new InvalidQueryParameterException(
                        "Invalid sort order: '" + order + "'. Allowed values are 'asc' or 'desc'."
                );
            }

            Comparator<Map<String, Object>> comparator =
                    (map1, map2) -> compareValues(map1.get(sortBy), map2.get(sortBy));

            if ("desc".equalsIgnoreCase(order)) {
                comparator = comparator.reversed();
            }

            List<Map<String, Object>> sorted = records.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());

            return QueryResponse.builder()
                    .sortedRecords(sorted)
                    .build();
        }

        // 3. Default: Return all records
        return QueryResponse.builder()
                .records(records)
                .build();
    }

    private Map<String, Object> deserializeJson(DatasetRecordEntity entity) {

        try {
            return objectMapper.readValue(
                    entity.getPayloadJson(),
                    new TypeReference<Map<String, Object>>() {}
            );

        } catch (JacksonException e) {
            throw new RuntimeException(
                    "Corrupted JSON data stored in database for record ID: " + entity.getId(), e
            );
        }
    }

    @SuppressWarnings("unchecked")
    private int compareValues(Object val1, Object val2) {

        if (val1 == null && val2 == null) return 0;
        if (val1 == null) return 1;
        if (val2 == null) return -1;

        // Compare numeric values
        if (val1 instanceof Number && val2 instanceof Number) {
            return Double.compare(
                    ((Number) val1).doubleValue(),
                    ((Number) val2).doubleValue()
            );
        }

        // Compare compatible Comparable objects
        if (val1 instanceof Comparable && val1.getClass().isInstance(val2)) {
            return ((Comparable<Object>) val1).compareTo(val2);
        }

        // Fallback to string comparison
        return val1.toString().compareTo(val2.toString());
    }
}

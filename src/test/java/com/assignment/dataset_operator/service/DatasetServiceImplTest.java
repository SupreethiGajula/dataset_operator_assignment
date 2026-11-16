
package com.assignment.dataset_operator.service;

import com.assignment.dataset_operator.dto.InsertRecordResponse;
import com.assignment.dataset_operator.dto.QueryResponse;
import com.assignment.dataset_operator.entity.DatasetRecordEntity;
import com.assignment.dataset_operator.exception.DatasetNotFoundException;
import com.assignment.dataset_operator.exception.InvalidQueryParameterException;
import com.assignment.dataset_operator.repository.DatasetRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatasetServiceImplTest {

    @Mock
    private DatasetRepository repository;

    private DatasetServiceImpl datasetService;

    private List<DatasetRecordEntity> mockEntities;

    @BeforeEach
    void setUp() {

        // Use a real Jackson 3 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        datasetService = new DatasetServiceImpl(repository, objectMapper);

        mockEntities = List.of(
                DatasetRecordEntity.builder()
                        .id(1L)
                        .datasetName("employee_dataset")
                        .payloadJson("{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}")
                        .build(),

                DatasetRecordEntity.builder()
                        .id(2L)
                        .datasetName("employee_dataset")
                        .payloadJson("{\"id\":2,\"name\":\"Jane Smith\",\"age\":25,\"department\":\"Engineering\"}")
                        .build(),

                DatasetRecordEntity.builder()
                        .id(3L)
                        .datasetName("employee_dataset")
                        .payloadJson("{\"id\":3,\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}")
                        .build()
        );
    }

    @Test
    @DisplayName("Should successfully insert a valid JSON record")
    void testInsertRecordSuccess() {

        Map<String, Object> payload = Map.of(
                "id", 1,
                "name", "John Doe",
                "age", 30,
                "department", "Engineering"
        );

        DatasetRecordEntity savedEntity = DatasetRecordEntity.builder()
                .id(1L)
                .datasetName("employee_dataset")
                .payloadJson("{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}")
                .build();

        when(repository.save(any(DatasetRecordEntity.class)))
                .thenReturn(savedEntity);

        InsertRecordResponse response =
                datasetService.insertRecord("employee_dataset", payload);

        assertNotNull(response);
        assertEquals("Record added successfully", response.getMessage());
        assertEquals("employee_dataset", response.getDataset());
        assertEquals(1L, response.getRecordId());

        verify(repository, times(1)).save(any(DatasetRecordEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when inserting empty payload")
    void testInsertEmptyRecordThrowsException() {

        assertThrows(InvalidQueryParameterException.class, () ->
                datasetService.insertRecord(
                        "employee_dataset",
                        Collections.emptyMap()
                )
        );

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should group records dynamically by specified field")
    void testQueryDatasetGroupBy() {

        when(repository.findByDatasetName("employee_dataset"))
                .thenReturn(mockEntities);

        QueryResponse response = datasetService.queryDataset(
                "employee_dataset", "department", null, "asc"
        );

        assertNotNull(response.getGroupedRecords());
        assertEquals(2, response.getGroupedRecords().size());

        assertEquals(
                2,
                response.getGroupedRecords().get("Engineering").size()
        );

        assertEquals(
                1,
                response.getGroupedRecords().get("Marketing").size()
        );
    }

    @Test
    @DisplayName("Should sort records in ascending order by numeric field")
    void testQueryDatasetSortByAscending() {

        when(repository.findByDatasetName("employee_dataset"))
                .thenReturn(mockEntities);

        QueryResponse response = datasetService.queryDataset(
                "employee_dataset", null, "age", "asc"
        );

        List<Map<String, Object>> sorted = response.getSortedRecords();

        assertNotNull(sorted);
        assertEquals(3, sorted.size());

        assertEquals("Jane Smith", sorted.get(0).get("name")); // age 25
        assertEquals("Alice Brown", sorted.get(1).get("name")); // age 28
        assertEquals("John Doe", sorted.get(2).get("name")); // age 30
    }

    @Test
    @DisplayName("Should sort records in descending order")
    void testQueryDatasetSortByDescending() {

        when(repository.findByDatasetName("employee_dataset"))
                .thenReturn(mockEntities);

        QueryResponse response = datasetService.queryDataset(
                "employee_dataset", null, "age", "desc"
        );

        List<Map<String, Object>> sorted = response.getSortedRecords();

        assertNotNull(sorted);
        assertEquals(3, sorted.size());

        assertEquals("John Doe", sorted.get(0).get("name")); // age 30
        assertEquals("Jane Smith", sorted.get(2).get("name")); // age 25
    }

    @Test
    @DisplayName("Should throw InvalidQueryParameterException for invalid sort order")
    void testQueryDatasetInvalidSortOrder() {

        when(repository.findByDatasetName("employee_dataset"))
                .thenReturn(mockEntities);

        assertThrows(InvalidQueryParameterException.class, () ->
                datasetService.queryDataset(
                        "employee_dataset", null, "age", "invalid_order"
                )
        );
    }

    @Test
    @DisplayName("Should throw DatasetNotFoundException when dataset has no records")
    void testQueryNonExistentDataset() {

        when(repository.findByDatasetName("unknown_dataset"))
                .thenReturn(Collections.emptyList());

        assertThrows(DatasetNotFoundException.class, () ->
                datasetService.queryDataset(
                        "unknown_dataset", "department", null, "asc"
                )
        );
    }
}

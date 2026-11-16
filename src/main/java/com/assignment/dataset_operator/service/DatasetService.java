package com.assignment.dataset_operator.service;

import com.assignment.dataset_operator.dto.InsertRecordResponse;
import com.assignment.dataset_operator.dto.QueryResponse;

import java.util.Map;

public interface DatasetService {

    InsertRecordResponse insertRecord(String datasetName, Map<String, Object> recordData);

    QueryResponse queryDataset(String datasetName, String groupBy, String sortBy, String order);
}
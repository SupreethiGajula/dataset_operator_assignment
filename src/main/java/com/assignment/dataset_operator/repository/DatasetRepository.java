package com.assignment.dataset_operator.repository;

import com.assignment.dataset_operator.entity.DatasetRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<DatasetRecordEntity, Long> {
    
    List<DatasetRecordEntity> findByDatasetName(String datasetName);
    
    boolean existsByDatasetName(String datasetName);
}
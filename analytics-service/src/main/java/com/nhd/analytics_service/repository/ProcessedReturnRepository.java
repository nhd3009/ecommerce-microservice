package com.nhd.analytics_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhd.analytics_service.entity.ProcessedReturn;

public interface ProcessedReturnRepository extends JpaRepository<ProcessedReturn, Long> {

}

package com.nhd.analytics_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhd.analytics_service.entity.ReturnTransaction;

public interface ReturnTransactionRepository extends JpaRepository<ReturnTransaction, Long> {

}

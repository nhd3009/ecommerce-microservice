package com.nhd.analytics_service.repository;

import com.nhd.analytics_service.entity.DailyRevenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyRevenueRepository extends JpaRepository<DailyRevenue, LocalDate> {
    List<DailyRevenue> findByDateBetweenOrderByDateAsc(LocalDate from, LocalDate to);
}
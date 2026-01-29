package com.nhd.analytics_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhd.analytics_service.entity.SalesTransaction;

public interface SalesTransactionRepository extends JpaRepository<SalesTransaction, Long> {
    @Query("""
        SELECT 
            COALESCE(SUM(st.revenue), 0),
            COALESCE(SUM(st.cost), 0),
            COALESCE(SUM(st.profit), 0)
        FROM SalesTransaction st
        WHERE st.orderDate BETWEEN :from AND :to
    """)
    List<Object[]> getSummary(LocalDate from, LocalDate to);

    @Query("""
        SELECT 
            st.productId,
            st.productName,
            SUM(st.quantity),
            SUM(st.revenue),
            SUM(st.profit)
        FROM SalesTransaction st
        WHERE st.orderDate BETWEEN :from AND :to
        GROUP BY st.productId, st.productName
        ORDER BY SUM(st.profit) DESC
    """)
    List<Object[]> getTopProductByProfit(
            LocalDate from, LocalDate to, Pageable pageable);

    @Query("""
            select 
                st.orderDate,
                sum(st.revenue),
                sum(st.cost),
                sum(st.profit),
                count(distinct st.orderId),
                sum(st.quantity)
            from SalesTransaction st
            group by st.orderDate
            """)
    List<Object[]> aggregateByDate();
}

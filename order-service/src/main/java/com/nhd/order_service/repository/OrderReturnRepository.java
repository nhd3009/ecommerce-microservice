package com.nhd.order_service.repository;

import com.nhd.order_service.entity.OrderReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderReturnRepository extends JpaRepository<OrderReturn, Long> {
    List<OrderReturn> findByOrderId(Long orderId);

    List<OrderReturn> findByUserId(Long userId);

    @Query("""
        select coalesce(sum(r.quantity), 0)
        from OrderReturn r
        where r.orderId = :orderId
          and r.orderItemId = :orderItemId
          and r.status in ('APPROVED', 'COMPLETED')
    """)
    int sumReturnedQuantity(
            @Param("orderId") Long orderId,
            @Param("orderItemId") Long orderItemId
    );
}

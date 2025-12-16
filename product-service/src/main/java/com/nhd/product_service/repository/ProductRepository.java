package com.nhd.product_service.repository;

import com.nhd.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
  Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
}

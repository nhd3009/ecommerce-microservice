package com.nhd.product_service.repository;

import com.nhd.commonlib.dto.enums.ProductStatus;
import com.nhd.product_service.entity.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
  Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);

  List<Product> findAllByCategoryId(Long categoryId); 

  Page<Product> findByCategoryIdAndStatus(
        Long categoryId,
        ProductStatus status,
        Pageable pageable
  );
}

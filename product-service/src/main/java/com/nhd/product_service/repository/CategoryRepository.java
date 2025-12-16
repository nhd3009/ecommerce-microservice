package com.nhd.product_service.repository;

import com.nhd.product_service.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
  Optional<Category> findByName(String name);
}

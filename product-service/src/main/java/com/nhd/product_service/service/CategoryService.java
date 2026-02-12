package com.nhd.product_service.service;

import com.nhd.commonlib.exception.BadRequestException;
import com.nhd.commonlib.exception.DuplicateException;
import com.nhd.commonlib.exception.ResourceNotFoundException;
import com.nhd.commonlib.response.ApiResponse;
import com.nhd.product_service.config.CacheInvalidationPublisher;
import com.nhd.product_service.dto.CacheInvalidationEvent;
import com.nhd.product_service.dto.CategoryDto;
import com.nhd.product_service.entity.Category;
import com.nhd.product_service.enums.CategoryStatus;
import com.nhd.product_service.mapper.CategoryMapper;
import com.nhd.product_service.repository.CategoryRepository;
import com.nhd.product_service.request.CreateCategoryRequest;
import com.nhd.product_service.request.UpdateCategoryRequest;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final CacheInvalidationPublisher cacheInvalidationPublisher;

  @Cacheable(value = "categories", key = "'all'")
  public List<CategoryDto> getAllCategories() {
    return categoryRepository.findAll().stream().map(CategoryMapper::toDto).toList();
  }

  @Caching(evict = {
    @CacheEvict(value = "categories", key = "'all'")
  })
  public CategoryDto create(CreateCategoryRequest request) {
    if (categoryRepository.findByName(request.getName()).isPresent()) {
      throw new DuplicateException("Category name already exists");
    }

    Category category = Category.builder()
        .name(request.getName())
        .description(request.getDescription())
        .status(request.getStatus())
        .build();

    Category saved = categoryRepository.save(category);
    return CategoryMapper.toDto(saved);
  }

  @Cacheable(value = "category", key = "#id")
  public CategoryDto getCategoryById(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    return CategoryMapper.toDto(category);
  }

  @Transactional
  @Caching(
    put = @CachePut(value = "category", key = "#id"),
    evict = {
      @CacheEvict(value = "categories", key = "'all'")
  })
  public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    category.setName(request.getName());
    category.setDescription(request.getDescription());
    category.setStatus(request.getStatus());
    Category updated = categoryRepository.save(category);
    CacheInvalidationEvent event = CacheInvalidationEvent.builder()
            .source("CATEGORY_SERVICE")
            .type("CATEGORY_UPDATED")
            .entityId(id)
            .timestamp(LocalDateTime.now())
            .build();
    cacheInvalidationPublisher.publish(event);
    return CategoryMapper.toDto(updated);
  }

  @Transactional
  public CategoryDto updateCategoryStatus(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    CategoryStatus currentStatus = category.getStatus();

    CategoryStatus newStatus = (currentStatus == CategoryStatus.ACTIVE)
        ? CategoryStatus.INACTIVE
        : CategoryStatus.ACTIVE;

    category.setStatus(newStatus);
    Category saved = categoryRepository.save(category);
    CacheInvalidationEvent event = CacheInvalidationEvent.builder()
            .source("CATEGORY_SERVICE")
            .type("CATEGORY_UPDATED")
            .entityId(id)
            .timestamp(LocalDateTime.now())
            .build();
    cacheInvalidationPublisher.publish(event);
    return CategoryMapper.toDto(saved);
  }

  @Caching(evict = {
    @CacheEvict(value = "category", key = "#id"),
    @CacheEvict(value = "categories", key = "'all'")
  })
  @Transactional
  public String deleteCategory(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

    if (category.getStatus() == CategoryStatus.ACTIVE) {
      throw new BadRequestException("Cannot delete an active category");
    }

    categoryRepository.delete(category);
    CacheInvalidationEvent event = CacheInvalidationEvent.builder()
        .source("CATEGORY_SERVICE")
        .type("CATEGORY_DELETED")
        .entityId(id)
        .timestamp(LocalDateTime.now())
        .build();
    cacheInvalidationPublisher.publish(event);
    return "Category with id " + id + " has been deleted";
  }

}

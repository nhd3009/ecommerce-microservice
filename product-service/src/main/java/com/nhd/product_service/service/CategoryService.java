package com.nhd.product_service.service;

import com.nhd.product_service.dto.CategoryDto;
import com.nhd.product_service.entity.Category;
import com.nhd.product_service.exception.DuplicateException;
import com.nhd.product_service.mapper.CategoryMapper;
import com.nhd.product_service.repository.CategoryRepository;
import com.nhd.product_service.request.CreateCategoryRequest;
import com.nhd.product_service.response.ApiResponse;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public ApiResponse<List<CategoryDto>> getAllCategories() {
    List<CategoryDto> categories = categoryRepository.findAll().stream().map(CategoryMapper::toDto).toList();
    return new ApiResponse<>(HttpStatus.OK.value(), "Categories retrieved successfully", categories);
  }

  public ApiResponse<CategoryDto> create(CreateCategoryRequest request) {
    if (categoryRepository.findByName(request.getName()).isPresent()) {
      throw new DuplicateException("Category name already exists");
    }

    Category category = Category.builder()
        .name(request.getName())
        .description(request.getDescription())
        .status(request.getStatus())
        .build();

    Category saved = categoryRepository.save(category);
    return new ApiResponse<>(HttpStatus.CREATED.value(), "Category created successfully", CategoryMapper.toDto(saved));
  }
}

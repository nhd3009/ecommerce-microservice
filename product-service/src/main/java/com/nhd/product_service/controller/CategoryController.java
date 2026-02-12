package com.nhd.product_service.controller;

import com.nhd.commonlib.response.ApiResponse;
import com.nhd.product_service.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhd.product_service.request.CreateCategoryRequest;
import com.nhd.product_service.request.UpdateCategoryRequest;
import com.nhd.product_service.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> create(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.OK.value(), "A category created successfully!", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> response = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Retrieved All Category Successfully!", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(@PathVariable("id") Long id){
        CategoryDto response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Category Retrieved Successfully!", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryDto response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "A category has been updated successfully!", response));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<CategoryDto>> toggleCategoryStatus(@PathVariable Long id) {
        CategoryDto response = categoryService.updateCategoryStatus(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Category has been updated with status: " + response.getStatus(), response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        String response = categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response, null));
    }
}

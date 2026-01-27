package com.nhd.product_service.controller;

import com.nhd.commonlib.exception.ResourceNotFoundException;
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
        try{
            CategoryDto response = categoryService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.OK.value(), "A category created successfully!", response));
        } catch (Exception e) {
            throw new RuntimeException("Errors when creating a category: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        try{
            List<CategoryDto> response = categoryService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Retrieved All Category Successfully!", response));
        } catch (Exception e) {
            throw new RuntimeException("Errors when fetch all category list: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(@PathVariable("id") Long id){
        try{
            CategoryDto response = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Category Retrieved Successfully!", response));
        } catch (ResourceNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errors when get category by id: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        try{
            CategoryDto response = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "A category has been updated successfully!", response));
        } catch (Exception e){
            throw new RuntimeException("Errors when updating a category: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<CategoryDto>> toggleCategoryStatus(@PathVariable Long id) {
        try{
            CategoryDto response = categoryService.updateCategoryStatus(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Category has been updated with status: " + response.getStatus(), response));
        } catch (Exception e){
            throw new RuntimeException("Errors when updating a category status: " + e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        try{
            String response = categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response, null));
        } catch (Exception e){
            throw new RuntimeException("Errors when deleting a category: " + e.getMessage());
        }
    }
}

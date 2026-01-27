package com.nhd.product_service.controller;

import com.nhd.commonlib.dto.ProductDto;
import com.nhd.commonlib.exception.ResourceNotFoundException;
import com.nhd.commonlib.response.ApiResponse;
import com.nhd.commonlib.response.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nhd.product_service.request.ProductRequest;
import com.nhd.product_service.request.ProductFilterRequest;
import com.nhd.product_service.service.FileStorageService;
import com.nhd.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try{
            PageResponse<ProductDto> response = productService.getAllProducts(page, size);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All products retrieved successfully!", response));
        } catch (Exception e) {
            throw new RuntimeException("Errors when retrieving all products: " + e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable("id") Long id) {
        try{
            ProductDto response = productService.getProductById(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "A product retrieved successfully!", response));
        }catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errors when retrieving a product: "+ e.getMessage());
        }

    }

    @PostMapping("/{id}/adjust-stock")
    public ResponseEntity<ApiResponse<String>> adjustProductStock(@PathVariable("id") Long id,
            @RequestParam("quantity") int quantity) {
        try{
            String response = productService.adjustStock(id, quantity);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response, null));
        } catch (Exception e) {
            throw new RuntimeException("Errors when adjusting product stock: " + e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try{
            PageResponse<ProductDto> response = productService.getProductsByCategory(categoryId, page, size);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All Products from categoryID: " + categoryId + " retrieved successfully!", response));
        } catch (Exception e) {
            throw new RuntimeException("Errors when retrieving products by category: " + e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try{
            ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                    .name(name)
                    .categoryId(categoryId)
                    .minPrice(minPrice)
                    .maxPrice(maxPrice)
                    .build();
            PageResponse<ProductDto> response = productService.getAllProductByFilter(filterRequest, page, size);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Products has been retrived successfull with filters", response));
        } catch (Exception e) {
            throw new RuntimeException("Errors when filtering all products: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @RequestPart("productInfo") ProductRequest productRequest,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try{
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbnailUrl = fileStorageService.saveFile(thumbnail);
                productRequest.setThumbnailUrl(thumbnailUrl);
            }

            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = fileStorageService.saveListFiles(images);
                productRequest.setImageUrls(imageUrls);
            }
            ProductDto response = productService.createProduct(productRequest);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "A product has been created successfully!", response));
        } catch (Exception e) {
            throw new RuntimeException("Errors when creating product: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @RequestPart("productInfo") ProductRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try{
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbnailUrl = fileStorageService.saveFile(thumbnail);
                request.setThumbnailUrl(thumbnailUrl);
            }

            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = fileStorageService.saveListFiles(images);
                request.setImageUrls(imageUrls);
            }
            ProductDto response = productService.updateProduct(id, request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "A Product has been updated successfully!", response));
        } catch (Exception e) {
            throw new RuntimeException("Errors when updating a product: " + e.getMessage());
        }

    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<ProductDto>> updateProductStatus(@PathVariable Long id) {
        try{
            ProductDto response = productService.updateStatusProduct(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Product has been updated with status: " + response.getStatus(), response));
        } catch (Exception e) {
            throw new RuntimeException("Errors when updating a product status: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        try{
            String response = productService.deleteProduct(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response, null));
        } catch (Exception e) {
            throw new RuntimeException("Errors when deleting a product status: " + e.getMessage());
        }
    }

}

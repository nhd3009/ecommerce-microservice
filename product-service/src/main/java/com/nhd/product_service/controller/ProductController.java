package com.nhd.product_service.controller;

import com.nhd.commonlib.dto.ProductDto;
import com.nhd.commonlib.dto.ProductOrderView;
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
        PageResponse<ProductDto> response = productService.getAllProducts(page, size);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All products retrieved successfully!", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable("id") Long id) {
        ProductDto response = productService.getProductById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "A product retrieved successfully!", response));
    }

    @PostMapping("/{id}/adjust-stock")
    public ResponseEntity<ApiResponse<String>> adjustProductStock(@PathVariable("id") Long id,
            @RequestParam("quantity") int quantity) {
        String response = productService.adjustStock(id, quantity);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response, null));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductDto> response = productService.getProductsByCategory(categoryId, page, size);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All Products from categoryID: " + categoryId + " retrieved successfully!", response));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ProductFilterRequest filterRequest = ProductFilterRequest.builder()
                .name(name)
                .categoryId(categoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
        PageResponse<ProductDto> response = productService.getAllProductByFilter(filterRequest, page, size);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Products has been retrieved successfully with filters", response));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @RequestPart("productInfo") ProductRequest productRequest,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        handleImage(productRequest, thumbnail, images);
        ProductDto response = productService.createProduct(productRequest);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "A product has been created successfully!", response));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @RequestPart("productInfo") ProductRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        handleImage(request, thumbnail, images);
        ProductDto response = productService.updateProduct(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "A Product has been updated successfully!", response));

    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<ProductDto>> updateProductStatus(@PathVariable Long id) {
        ProductDto response = productService.updateStatusProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Product has been updated with status: " + response.getStatus(), response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        String response = productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response, null));
    }

    @GetMapping("/internal/{id}")
    public ResponseEntity<ApiResponse<ProductOrderView>> getInternalProductForOrder(@PathVariable Long id){
        ProductOrderView result = productService.getProductForOrder(id);
        ApiResponse<ProductOrderView> response = new ApiResponse<>(HttpStatus.OK.value(), "Internal Product Retrieved Successfully!", result);
        return ResponseEntity.ok(response);
    }

    private void handleImage(@RequestPart("productInfo") ProductRequest request, @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = fileStorageService.saveFile(thumbnail);
            request.setThumbnailUrl(thumbnailUrl);
        }

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = fileStorageService.saveListFiles(images);
            request.setImageUrls(imageUrls);
        }
    }
}

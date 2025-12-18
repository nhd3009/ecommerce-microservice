package com.nhd.product_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nhd.product_service.dto.ProductDto;
import com.nhd.product_service.entity.Category;
import com.nhd.product_service.entity.Product;
import com.nhd.product_service.entity.ProductImage;
import com.nhd.product_service.enums.CategoryStatus;
import com.nhd.product_service.enums.ProductStatus;
import com.nhd.product_service.exception.BadRequestException;
import com.nhd.product_service.exception.ResourceNotFoundException;
import com.nhd.product_service.mapper.ProductMapper;
import com.nhd.product_service.repository.CategoryRepository;
import com.nhd.product_service.repository.ProductRepository;
import com.nhd.product_service.request.ProductRequest;
import com.nhd.product_service.request.ProductFilterRequest;
import com.nhd.product_service.response.ApiResponse;
import com.nhd.product_service.response.PageResponse;
import com.nhd.product_service.specification.ProductSpecification;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository,
            FileStorageService fileStorageService) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    public ApiResponse<ProductDto> getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID: " + id + "not found"));
        ProductDto productDto = ProductMapper.toDto(product);
        return new ApiResponse<>(HttpStatus.OK.value(), "Product retrieved successfully", productDto);
    }

    public ApiResponse<PageResponse<ProductDto>> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Product> products = productRepository.findAll(pageable);
        List<ProductDto> productDtos = products.getContent().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
        PageResponse<ProductDto> response = PageResponse.<ProductDto>builder()
                .data(productDtos)
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .currentPage(products.getNumber())
                .pageSize(products.getSize())
                .build();

        return new ApiResponse<>(HttpStatus.OK.value(), "Products retrieved successfully", response);
    }

    public ApiResponse<PageResponse<ProductDto>> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productRepository.findAllByCategoryId(categoryId, pageable);
        List<ProductDto> productDtos = products.getContent().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
        PageResponse<ProductDto> response = PageResponse.<ProductDto>builder()
                .data(productDtos)
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .currentPage(products.getNumber())
                .pageSize(products.getSize())
                .build();
        return new ApiResponse<>(HttpStatus.OK.value(), "Products retrieved successfully", response);
    }

    public ApiResponse<PageResponse<ProductDto>> getAllProductByFilter(ProductFilterRequest filterRequest, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Product> spec = ProductSpecification.filter(filterRequest);

        Page<Product> products = productRepository.findAll(spec, pageable);
        List<ProductDto> productDtos = products.getContent().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());

        PageResponse<ProductDto> response = PageResponse.<ProductDto>builder()
                .data(productDtos)
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .currentPage(products.getNumber())
                .pageSize(products.getSize())
                .build();
        return new ApiResponse<>(HttpStatus.OK.value(), "Products retrieved successfully", response);
    }

    public ApiResponse<ProductDto> createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category with ID: " + request.getCategoryId() + " not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .thumbnailUrl(request.getThumbnailUrl())
                .category(category)
                .status(request.getStatus())
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImage> productImages = request.getImageUrls().stream()
                    .map(url -> ProductImage.builder()
                            .imageUrl(url)
                            .product(product)
                            .build())
                    .collect(Collectors.toList());
            product.setImages(productImages);
        }

        Product savedProduct = productRepository.save(product);
        ProductDto productDto = ProductMapper.toDto(savedProduct);
        return new ApiResponse<>(HttpStatus.CREATED.value(), "Product created successfully", productDto);
    }

    @Transactional
    public ApiResponse<ProductDto> updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        List<String> oldImageUrls = product.getImages() != null
                ? product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                : new ArrayList<>();

        if (request.getName() != null)
            product.setName(request.getName());
        if (request.getDescription() != null)
            product.setDescription(request.getDescription());
        if (request.getPrice() != null)
            product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null)
            product.setStockQuantity(request.getStockQuantity());
        if (request.getThumbnailUrl() != null)
            product.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getStatus() != null)
            product.setStatus(request.getStatus());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getImageUrls() != null) {
            List<ProductImage> newImages = request.getImageUrls().stream()
                    .map(url -> ProductImage.builder()
                            .imageUrl(url)
                            .product(product)
                            .build())
                    .collect(Collectors.toList());

            List<String> removedImages = oldImageUrls.stream()
                    .filter(old -> !request.getImageUrls().contains(old))
                    .collect(Collectors.toList());

            removedImages.forEach(fileStorageService::deleteFile);

            product.getImages().clear();
            product.getImages().addAll(newImages);
        }

        Product updatedProduct = productRepository.save(product);

        ProductDto dto = ProductMapper.toDto(updatedProduct);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Product updated successfully",
                dto);
    }

    public ApiResponse<String> updateStatusProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        ProductStatus currentStatus = product.getStatus();
        ProductStatus newStatus = (currentStatus == ProductStatus.ACTIVE)
                ? ProductStatus.INACTIVE
                : ProductStatus.ACTIVE;
        product.setStatus(newStatus);
        productRepository.save(product);
        return new ApiResponse<String>(
                HttpStatus.OK.value(),
                "Product status updated successfully",
                "Product status is now " + newStatus.name());
    }

    @Transactional
    public ApiResponse<String> decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        int currentStock = product.getStockQuantity();
        if (currentStock < quantity) {
            throw new BadRequestException("Not enough stock for product id: " + productId);
        }

        int newStock = currentStock - quantity;
        product.setStockQuantity(newStock);

        if (newStock == 0) {
            product.setStatus(ProductStatus.INACTIVE);
        }

        productRepository.save(product);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Stock updated successfully",
                "Product stock now: " + newStock + ", status: " + product.getStatus());
    }

    public ApiResponse<String> deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (product.getStatus() == ProductStatus.ACTIVE) {
            throw new BadRequestException("Cannot delete an active product");
        }

        productRepository.delete(product);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Product deleted successfully",
                "Product with id " + id + " has been deleted");
    }
}

package com.nhd.product_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nhd.commonlib.dto.ProductDto;
import com.nhd.commonlib.dto.ProductOrderView;
import com.nhd.commonlib.dto.enums.ProductStatus;
import com.nhd.commonlib.exception.BadRequestException;
import com.nhd.commonlib.exception.ResourceNotFoundException;
import com.nhd.commonlib.response.PageResponse;
import com.nhd.product_service.dto.AdminProductDto;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nhd.product_service.entity.Category;
import com.nhd.product_service.entity.Product;
import com.nhd.product_service.entity.ProductImage;
import com.nhd.product_service.mapper.ProductMapper;
import com.nhd.product_service.repository.CategoryRepository;
import com.nhd.product_service.repository.ProductRepository;
import com.nhd.product_service.request.ProductRequest;
import com.nhd.product_service.request.ProductFilterRequest;
import com.nhd.product_service.specification.ProductSpecification;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Cacheable(value = "product", key = "#id")
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID: " + id + "not found"));
        return ProductMapper.toDto(product);
    }

    @Cacheable(value = "product_pages", key = "'page_'+#page+'_size_'+#size")
    public PageResponse<ProductDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Product> products = productRepository.findAll(pageable);
        return getProductPageResponse(products);
    }

    @Cacheable(value = "products_by_category", key = "'category_'+#categoryId+'_page_'+#page+'_size_'+#size")
    public PageResponse<ProductDto> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productRepository.findAllByCategoryId(categoryId, pageable);
        return getProductPageResponse(products);
    }

    public PageResponse<ProductDto> getAllProductByFilter(ProductFilterRequest filterRequest, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Product> spec = ProductSpecification.filter(filterRequest);

        Page<Product> products = productRepository.findAll(spec, pageable);
        return getProductPageResponse(products);
    }

    public PageResponse<AdminProductDto> getAllAdminProductByFilter(ProductFilterRequest filterRequest, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Product> spec = ProductSpecification.filter(filterRequest);

        Page<Product> products = productRepository.findAll(spec, pageable);

        List<AdminProductDto> productDtos = products.getContent().stream()
                .map(ProductMapper::toAdminDto)
                .collect(Collectors.toList());

        return PageResponse.<AdminProductDto>builder()
                .data(productDtos)
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .currentPage(products.getNumber())
                .pageSize(products.getSize())
                .build();
    }

    @Cacheable(value = "product_admin_pages", key = "'page_'+#page+'_size_'+#size")
    public PageResponse<AdminProductDto> getAllAdminProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Product> products = productRepository.findAll(pageable);
        List<AdminProductDto> productDtos = products.getContent().stream()
                .map(ProductMapper::toAdminDto)
                .collect(Collectors.toList());

        return PageResponse.<AdminProductDto>builder()
                .data(productDtos)
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .currentPage(products.getNumber())
                .pageSize(products.getSize())
                .build();
    }

    @Caching(evict = {
        @CacheEvict(value = "product_pages", allEntries = true),
        @CacheEvict(value = "products_by_category", allEntries = true)
    })
    public ProductDto createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category with ID: " + request.getCategoryId() + " not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .importPrice(request.getImportPrice())
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
        return ProductMapper.toDto(savedProduct);
    }

    @Transactional
    @Caching(
        put = @CachePut(value = "product", key = "#id"),
        evict = {
                @CacheEvict(value = "product_pages", allEntries = true),
                @CacheEvict(value = "products_by_category", allEntries = true)
        }
    )
    public ProductDto updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        List<String> oldImageUrls = product.getImages() != null
                ? product.getImages().stream().map(ProductImage::getImageUrl).toList()
                : new ArrayList<>();

        if (request.getName() != null)
            product.setName(request.getName());
        if (request.getDescription() != null)
            product.setDescription(request.getDescription());
        if (request.getPrice() != null)
            product.setPrice(request.getPrice());
        if (request.getImportPrice() != null)
            product.setImportPrice(request.getImportPrice());
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
                    .toList();

            List<String> removedImages = oldImageUrls.stream()
                    .filter(old -> !request.getImageUrls().contains(old))
                    .toList();

            removedImages.forEach(fileStorageService::deleteFile);

            product.getImages().clear();
            product.getImages().addAll(newImages);
        }

        Product updatedProduct = productRepository.save(product);

        return ProductMapper.toDto(updatedProduct);
    }

    @Transactional
    @Caching(
        put = @CachePut(value = "product", key = "#id"),
        evict = {
                @CacheEvict(value = "product_pages", allEntries = true),
                @CacheEvict(value = "products_by_category", allEntries = true)
        }
    )
    public ProductDto updateStatusProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        ProductStatus currentStatus = product.getStatus();
        ProductStatus newStatus = (currentStatus == ProductStatus.ACTIVE)
                ? ProductStatus.INACTIVE
                : ProductStatus.ACTIVE;
        product.setStatus(newStatus);
        Product saved = productRepository.save(product);
        return ProductMapper.toDto(saved);
    }

    @Transactional
    public String adjustStock(Long productId, int quantity) {
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

        return "Product stock now: " + newStock + ", status: " + product.getStatus();
    }

    @Caching(evict = {
        @CacheEvict(value = "product", key = "#id"),
        @CacheEvict(value = "product_pages", allEntries = true),
        @CacheEvict(value = "products_by_category", allEntries = true)
    })
    @Transactional
    public String deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (product.getStatus() == ProductStatus.ACTIVE) {
            throw new BadRequestException("Cannot delete an active product");
        }

        productRepository.delete(product);

        return "Product with id " + id + " has been deleted";
    }

    public ProductOrderView getProductForOrder(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with ID: " + id + "not found"));
        return ProductOrderView.builder()
                .productId(product.getId())
                .productName(product.getName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sellPrice(product.getPrice())
                .importPrice(product.getImportPrice())
                .stockQuantity(product.getStockQuantity())
        .build();
    }

    @NonNull
    public PageResponse<ProductDto> getProductPageResponse(Page<Product> products) {
        List<ProductDto> productDtos = products.getContent().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());

        return PageResponse.<ProductDto>builder()
                .data(productDtos)
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .currentPage(products.getNumber())
                .pageSize(products.getSize())
                .build();
    }
}

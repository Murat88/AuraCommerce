package com.auracart.catalog.controller;

import com.auracart.catalog.dto.CreateProductRequest;
import com.auracart.catalog.dto.ProductResponse;
import com.auracart.catalog.entity.Product;
import com.auracart.catalog.service.CreateProductCommand;
import com.auracart.catalog.service.ProductCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ürün (Product) kaynağı için REST API uç noktalarını sunar.
 * <p>
 * Tenant yönlendirmesi {@code TenantInterceptor} tarafından request seviyesinde
 * (X-Tenant-ID header'ı üzerinden) örtük olarak yönetildiği için burada
 * herhangi bir ek tenant bağlantısına gerek yoktur.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductCommandService productCommandService;

    /**
     * Yeni bir ürün oluşturur.
     *
     * @param request istemciden gelen ürün oluşturma isteği
     * @return oluşturulan ürünü temsil eden {@link ProductResponse}, 201 Created ile birlikte
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
                request.categoryId(),
                request.brandId(),
                request.name(),
                request.slug(),
                request.description(),
                request.productType(),
                request.status()
        );

        Product createdProduct = productCommandService.createProduct(command);

        ProductResponse response = ProductResponse.from(createdProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


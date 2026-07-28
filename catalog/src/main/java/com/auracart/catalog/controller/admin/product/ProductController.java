package com.auracart.catalog.controller.admin.product;

import com.auracart.catalog.dto.admin.product.CreateProductRequest;
import com.auracart.catalog.dto.admin.product.ProductResponse;
import com.auracart.catalog.service.product.CreateProductCommand;
import com.auracart.catalog.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product/create")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Ürün (Product) kaynağı için REST API uç noktaları")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Yeni ürün oluşturur", description = "Verilen bilgilerle aktif tenant veritabanında yeni bir ürün oluşturur.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ürün başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {

        var command = new CreateProductCommand(
                request.categoryId(),
                request.brandId(),
                request.name(),
                request.slug(),
                request.description(),
                request.productType(),
                request.status()
        );

        var createdProduct = productService.createProduct(command);

        var response = ProductResponse.from(createdProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


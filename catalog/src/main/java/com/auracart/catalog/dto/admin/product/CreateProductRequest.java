package com.auracart.catalog.dto.admin.product;

import com.auracart.catalog.entity.product.ProductStatus;
import com.auracart.catalog.entity.product.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Yeni bir ürün oluşturma isteği için kullanılan istemci (client) DTO'su.
 * Controller katmanında {@code @Valid} ile doğrulanır ve ardından
 * {@link com.auracart.catalog.service.product.CreateProductCommand} nesnesine map edilir.
 */
public record CreateProductRequest(

        @NotNull(message = "Kategori ID boş olamaz")
        UUID categoryId,

        UUID brandId,

        @NotBlank(message = "Ürün adı boş olamaz")
        String name,

        @NotBlank(message = "Slug boş olamaz")
        String slug,

        String description,

        @NotNull(message = "Ürün tipi boş olamaz")
        ProductType productType,

        @NotNull(message = "Ürün durumu boş olamaz")
        ProductStatus status
) {
}


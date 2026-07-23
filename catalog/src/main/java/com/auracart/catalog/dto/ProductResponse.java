package com.auracart.catalog.dto;

import com.auracart.catalog.entity.Product;
import com.auracart.catalog.entity.ProductStatus;
import com.auracart.catalog.entity.ProductType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * İstemciye dönülen ürün bilgisi. {@link Product} entity'sinin dışa açılması
 * istenen alanlarını içerir.
 */
public record ProductResponse(
        UUID id,
        UUID categoryId,
        UUID brandId,
        String name,
        String slug,
        String description,
        ProductType productType,
        ProductStatus status,
        LocalDateTime createdAt
) {

    /**
     * {@link Product} entity'sinden {@link ProductResponse} oluşturan statik factory metodu.
     *
     * @param product oluşturulan/kaydedilen ürün entity'si
     * @return istemciye dönülecek DTO
     */
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCategoryId(),
                product.getBrandId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getProductType(),
                product.getStatus(),
                product.getInsertedAt()
        );
    }
}


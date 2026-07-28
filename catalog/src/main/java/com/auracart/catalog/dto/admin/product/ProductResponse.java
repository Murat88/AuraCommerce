package com.auracart.catalog.dto.admin.product;

import com.auracart.catalog.entity.product.Product;
import com.auracart.catalog.entity.product.ProductStatus;
import com.auracart.catalog.entity.product.ProductType;

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


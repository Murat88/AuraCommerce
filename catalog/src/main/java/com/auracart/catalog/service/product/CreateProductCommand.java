package com.auracart.catalog.service.product;

import com.auracart.catalog.entity.product.ProductStatus;
import com.auracart.catalog.entity.product.ProductType;

import java.util.UUID;

/**
 * Yeni bir ürün oluşturmak için gerekli girdi verilerini taşıyan komut nesnesi.
 */
public record CreateProductCommand(
        UUID categoryId,
        UUID brandId,
        String name,
        String slug,
        String description,
        ProductType productType,
        ProductStatus status
) {
}


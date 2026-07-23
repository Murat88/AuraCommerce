package com.auracart.catalog.service;

import com.auracart.catalog.entity.ProductStatus;
import com.auracart.catalog.entity.ProductType;

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


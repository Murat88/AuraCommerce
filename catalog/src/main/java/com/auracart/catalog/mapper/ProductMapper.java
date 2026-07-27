package com.auracart.catalog.mapper;

import com.auracart.catalog.entity.Product;
import com.auracart.catalog.service.CreateProductCommand;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductCommand command) {
        var product = new Product();
        product.setCategoryId(command.categoryId());
        product.setBrandId(command.brandId());
        product.setName(command.name());
        product.setSlug(command.slug());
        product.setDescription(command.description());
        product.setProductType(command.productType());
        product.setStatus(command.status());
        return product;
    }
}


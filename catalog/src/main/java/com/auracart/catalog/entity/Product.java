package com.auracart.catalog.entity;

import com.auracart.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Ürün (Product) bilgisi. Soft delete için status alanı ARCHIVED olduğunda
 * kayıt varsayılan sorgulardan hariç tutulur.
 */
@Getter
@Setter
@Entity
@Table(name = "products",schema = "catalog")
@SQLRestriction("status != 'ARCHIVED'")
public class Product extends BaseEntity {

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "brand_id")
    private UUID brandId;

    @Column(name = "name")
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "product_type")
    private ProductType productType;

    @Column(name = "tax_class_id")
    private UUID taxClassId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private ProductStatus status;
}


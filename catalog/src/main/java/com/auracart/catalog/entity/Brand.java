package com.auracart.catalog.entity;

import com.auracart.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Marka (Brand) bilgisi. Soft delete için is_active flag'i kullanılır.
 */
@Getter
@Setter
@Entity
@Table(name = "brands",schema = "catalog")
@SQLRestriction("is_active = true")
public class Brand extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;
}


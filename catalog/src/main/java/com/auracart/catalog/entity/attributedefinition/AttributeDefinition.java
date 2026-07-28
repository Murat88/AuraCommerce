package com.auracart.catalog.entity.attributedefinition;

import com.auracart.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Ürün varyantlarında kullanılan özellik (attribute) tanımı. Örn: Renk, Beden.
 */
@Getter
@Setter
@Entity
@Table(name = "attribute_definitions", schema = "catalog")
public class AttributeDefinition extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AttributeType type;
}


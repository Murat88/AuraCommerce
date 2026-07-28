package com.auracart.catalog.entity.attributevalue;

import com.auracart.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Bir {@link AttributeDefinition}'a ait somut değer. Örn: Kırmızı, XL, #FF0000.
 */
@Getter
@Setter
@Entity
@Table(name = "attribute_values", schema = "catalog")
public class AttributeValue extends BaseEntity {

    @Column(name = "attribute_definition_id")
    private UUID attributeDefinitionId;

    @Column(name = "value")
    private String value;
}


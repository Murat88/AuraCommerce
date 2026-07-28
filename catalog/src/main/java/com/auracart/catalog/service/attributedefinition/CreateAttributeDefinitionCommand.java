package com.auracart.catalog.service.attributedefinition;

import com.auracart.catalog.entity.attributedefinition.AttributeType;

/**
 * Yeni bir attribute definition oluşturmak için gerekli girdi verilerini taşıyan komut nesnesi.
 */
public record CreateAttributeDefinitionCommand(
        String name,
        AttributeType type
) {
}


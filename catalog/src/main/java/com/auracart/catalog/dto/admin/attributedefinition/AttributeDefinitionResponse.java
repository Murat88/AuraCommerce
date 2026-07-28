package com.auracart.catalog.dto.admin.attributedefinition;

import com.auracart.catalog.entity.attributedefinition.AttributeDefinition;
import com.auracart.catalog.entity.attributedefinition.AttributeType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * İstemciye dönülen attribute definition bilgisi. {@link AttributeDefinition} entity'sinin
 * dışa açılması istenen alanlarını içerir.
 */
public record AttributeDefinitionResponse(
        UUID id,
        String name,
        AttributeType type,
        LocalDateTime createdAt
) {

    public static AttributeDefinitionResponse from(AttributeDefinition attributeDefinition) {
        return new AttributeDefinitionResponse(
                attributeDefinition.getId(),
                attributeDefinition.getName(),
                attributeDefinition.getType(),
                attributeDefinition.getInsertedAt()
        );
    }
}


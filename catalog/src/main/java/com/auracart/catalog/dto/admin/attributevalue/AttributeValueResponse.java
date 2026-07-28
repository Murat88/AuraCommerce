package com.auracart.catalog.dto.admin.attributevalue;

import com.auracart.catalog.entity.attributevalue.AttributeValue;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * İstemciye dönülen attribute value bilgisi. {@link AttributeValue} entity'sinin
 * dışa açılması istenen alanlarını içerir.
 */
public record AttributeValueResponse(
        UUID id,
        UUID attributeDefinitionId,
        String value,
        LocalDateTime createdAt
) {

    public static AttributeValueResponse from(AttributeValue attributeValue) {
        return new AttributeValueResponse(
                attributeValue.getId(),
                attributeValue.getAttributeDefinitionId(),
                attributeValue.getValue(),
                attributeValue.getInsertedAt()
        );
    }
}


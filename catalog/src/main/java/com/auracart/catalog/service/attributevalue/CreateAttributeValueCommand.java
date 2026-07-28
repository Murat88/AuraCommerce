package com.auracart.catalog.service.attributevalue;

import java.util.UUID;

/**
 * Yeni bir attribute value oluşturmak için gerekli girdi verilerini taşıyan komut nesnesi.
 */
public record CreateAttributeValueCommand(
        UUID attributeDefinitionId,
        String value
) {
}


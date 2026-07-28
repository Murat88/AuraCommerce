package com.auracart.catalog.dto.admin.attributevalue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Yeni bir attribute value oluşturma isteği için kullanılan istemci (client) DTO'su.
 * Controller katmanında {@code @Valid} ile doğrulanır ve ardından
 * {@link com.auracart.catalog.service.attributevalue.CreateAttributeValueCommand} nesnesine map edilir.
 */
public record CreateAttributeValueRequest(

        @NotNull(message = "Attribute definition ID boş olamaz")
        UUID attributeDefinitionId,

        @NotBlank(message = "Attribute değeri boş olamaz")
        String value
) {
}


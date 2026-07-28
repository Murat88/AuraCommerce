package com.auracart.catalog.dto.admin.attributedefinition;

import com.auracart.catalog.entity.attributedefinition.AttributeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Yeni bir attribute definition oluşturma isteği için kullanılan istemci (client) DTO'su.
 * Controller katmanında {@code @Valid} ile doğrulanır ve ardından
 * {@link com.auracart.catalog.service.attributedefinition.CreateAttributeDefinitionCommand} nesnesine map edilir.
 */
public record CreateAttributeDefinitionRequest(

        @NotBlank(message = "Attribute adı boş olamaz")
        String name,

        @NotNull(message = "Attribute tipi boş olamaz")
        AttributeType type
) {
}


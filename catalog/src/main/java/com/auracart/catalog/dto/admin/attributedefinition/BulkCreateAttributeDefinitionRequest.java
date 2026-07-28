package com.auracart.catalog.dto.admin.attributedefinition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Toplu (bulk) attribute definition oluşturma isteği. Her bir eleman ayrı ayrı doğrulanır.
 */
public record BulkCreateAttributeDefinitionRequest(

        @NotEmpty(message = "Attribute definition listesi boş olamaz")
        @Valid
        List<CreateAttributeDefinitionRequest> definitions
) {
}


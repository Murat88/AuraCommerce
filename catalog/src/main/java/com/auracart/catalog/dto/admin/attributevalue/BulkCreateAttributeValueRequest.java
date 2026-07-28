package com.auracart.catalog.dto.admin.attributevalue;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Toplu (bulk) attribute value oluşturma isteği. Her bir eleman ayrı ayrı doğrulanır.
 */
public record BulkCreateAttributeValueRequest(

        @NotEmpty(message = "Attribute value listesi boş olamaz")
        @Valid
        List<CreateAttributeValueRequest> values
) {
}


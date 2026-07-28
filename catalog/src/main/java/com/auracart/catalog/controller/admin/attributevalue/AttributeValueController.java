package com.auracart.catalog.controller.admin.attributevalue;

import com.auracart.catalog.dto.admin.attributevalue.AttributeValueResponse;
import com.auracart.catalog.dto.admin.attributevalue.BulkCreateAttributeValueRequest;
import com.auracart.catalog.dto.admin.attributevalue.CreateAttributeValueRequest;
import com.auracart.catalog.service.attributevalue.AttributeValueService;
import com.auracart.catalog.service.attributevalue.CreateAttributeValueCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attribute-values")
@RequiredArgsConstructor
@Tag(name = "AttributeValue", description = "Attribute value kaynağı için REST API uç noktaları")
public class AttributeValueController {

    private final AttributeValueService attributeValueService;

    @PostMapping
    @Operation(summary = "Yeni bir attribute value oluşturur", description = "Verilen bilgilerle aktif tenant veritabanında tek bir attribute value oluşturur.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Attribute value başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi")
    })
    public ResponseEntity<AttributeValueResponse> createAttributeValue(@Valid @RequestBody CreateAttributeValueRequest request) {
        var command = new CreateAttributeValueCommand(request.attributeDefinitionId(), request.value());
        var createdAttributeValue = attributeValueService.createAttributeValue(command);
        var response = AttributeValueResponse.from(createdAttributeValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Toplu attribute value oluşturur", description = "Verilen listedeki her bir attribute value'yu aktif tenant veritabanında oluşturur.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Attribute value'lar başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi")
    })
    public ResponseEntity<List<AttributeValueResponse>> createAttributeValues(@Valid @RequestBody BulkCreateAttributeValueRequest request) {
        var commands = request.values().stream()
                .map(item -> new CreateAttributeValueCommand(item.attributeDefinitionId(), item.value()))
                .toList();

        var createdAttributeValues = attributeValueService.createAttributeValues(commands);

        var response = createdAttributeValues.stream()
                .map(AttributeValueResponse::from)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/definition/{attributeDefinitionId}")
    @Operation(summary = "Bir attribute definition'a ait tüm değerleri listeler")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attribute value listesi başarıyla getirildi")
    })
    public ResponseEntity<List<AttributeValueResponse>> getAttributeValuesByDefinition(@PathVariable UUID attributeDefinitionId) {
        var attributeValues = attributeValueService.getAttributeValuesByDefinition(attributeDefinitionId);
        var response = attributeValues.stream()
                .map(AttributeValueResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID'ye göre bir attribute value getirir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attribute value başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Attribute value bulunamadı")
    })
    public ResponseEntity<AttributeValueResponse> getAttributeValue(@PathVariable UUID id) {
        var attributeValue = attributeValueService.getAttributeValue(id);
        return ResponseEntity.ok(AttributeValueResponse.from(attributeValue));
    }
}


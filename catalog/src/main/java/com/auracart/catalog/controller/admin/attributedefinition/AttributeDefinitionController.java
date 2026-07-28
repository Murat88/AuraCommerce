package com.auracart.catalog.controller.admin.attributedefinition;

import com.auracart.catalog.dto.admin.attributedefinition.AttributeDefinitionResponse;
import com.auracart.catalog.dto.admin.attributedefinition.BulkCreateAttributeDefinitionRequest;
import com.auracart.catalog.dto.admin.attributedefinition.CreateAttributeDefinitionRequest;
import com.auracart.catalog.service.attributedefinition.AttributeDefinitionService;
import com.auracart.catalog.service.attributedefinition.CreateAttributeDefinitionCommand;
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
@RequestMapping("/api/v1/attribute-definitions")
@RequiredArgsConstructor
@Tag(name = "AttributeDefinition", description = "Attribute definition kaynağı için REST API uç noktaları")
public class AttributeDefinitionController {

    private final AttributeDefinitionService attributeDefinitionService;

    @PostMapping
    @Operation(summary = "Yeni bir attribute definition oluşturur", description = "Verilen bilgilerle aktif tenant veritabanında tek bir attribute definition oluşturur.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Attribute definition başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi")
    })
    public ResponseEntity<AttributeDefinitionResponse> createAttributeDefinition(@Valid @RequestBody CreateAttributeDefinitionRequest request) {
        var command = new CreateAttributeDefinitionCommand(request.name(), request.type());
        var createdAttributeDefinition = attributeDefinitionService.createAttributeDefinition(command);
        var response = AttributeDefinitionResponse.from(createdAttributeDefinition);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Toplu attribute definition oluşturur", description = "Verilen listedeki her bir attribute definition'ı aktif tenant veritabanında oluşturur.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Attribute definition'lar başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek gövdesi")
    })
    public ResponseEntity<List<AttributeDefinitionResponse>> createAttributeDefinitions(@Valid @RequestBody BulkCreateAttributeDefinitionRequest request) {
        var commands = request.definitions().stream()
                .map(item -> new CreateAttributeDefinitionCommand(item.name(), item.type()))
                .toList();

        var createdAttributeDefinitions = attributeDefinitionService.createAttributeDefinitions(commands);

        var response = createdAttributeDefinitions.stream()
                .map(AttributeDefinitionResponse::from)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Tüm attribute definition'ları listeler")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attribute definition listesi başarıyla getirildi")
    })
    public ResponseEntity<List<AttributeDefinitionResponse>> getAllAttributeDefinitions() {
        var attributeDefinitions = attributeDefinitionService.getAllAttributeDefinitions();
        var response = attributeDefinitions.stream()
                .map(AttributeDefinitionResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID'ye göre bir attribute definition getirir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attribute definition başarıyla getirildi"),
            @ApiResponse(responseCode = "404", description = "Attribute definition bulunamadı")
    })
    public ResponseEntity<AttributeDefinitionResponse> getAttributeDefinition(@PathVariable UUID id) {
        var attributeDefinition = attributeDefinitionService.getAttributeDefinition(id);
        return ResponseEntity.ok(AttributeDefinitionResponse.from(attributeDefinition));
    }
}


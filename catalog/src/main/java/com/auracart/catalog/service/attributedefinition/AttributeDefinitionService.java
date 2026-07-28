package com.auracart.catalog.service.attributedefinition;

import com.auracart.catalog.entity.attributedefinition.AttributeDefinition;
import com.auracart.catalog.mapper.attributedefinition.AttributeDefinitionMapper;
import com.auracart.catalog.repository.attributedefinition.AttributeDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttributeDefinitionService {

    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final AttributeDefinitionMapper attributeDefinitionMapper;

    @Transactional
    public AttributeDefinition createAttributeDefinition(CreateAttributeDefinitionCommand command) {
        validateCommand(command);

        // İsim benzersizliği bir iş kuralıdır, validate metodunda değil burada kontrol edilir.
        if (attributeDefinitionRepository.existsByNameIgnoreCase(command.name())) {
            throw new IllegalStateException("Bu attribute adı zaten kullanılıyor: " + command.name());
        }

        var attributeDefinition = attributeDefinitionMapper.toEntity(command);
        return attributeDefinitionRepository.save(attributeDefinition);
    }

    @Transactional
    public List<AttributeDefinition> createAttributeDefinitions(List<CreateAttributeDefinitionCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            throw new IllegalArgumentException("Attribute definition listesi boş olamaz");
        }

        return commands.stream()
                .map(this::createAttributeDefinition)
                .toList();
    }

    public AttributeDefinition getAttributeDefinition(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Attribute definition ID boş olamaz");
        }
        return attributeDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Attribute definition bulunamadı: " + id));
    }

    public List<AttributeDefinition> getAllAttributeDefinitions() {
        return attributeDefinitionRepository.findAll();
    }

    private void validateCommand(CreateAttributeDefinitionCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Attribute definition oluşturma komutu boş olamaz");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new IllegalArgumentException("Attribute adı boş olamaz");
        }
        if (command.type() == null) {
            throw new IllegalArgumentException("Attribute tipi boş olamaz");
        }
    }
}


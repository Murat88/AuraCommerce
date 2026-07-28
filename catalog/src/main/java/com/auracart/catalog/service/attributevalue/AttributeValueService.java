package com.auracart.catalog.service.attributevalue;

import com.auracart.catalog.entity.attributevalue.AttributeValue;
import com.auracart.catalog.mapper.attributevalue.AttributeValueMapper;
import com.auracart.catalog.repository.attributedefinition.AttributeDefinitionRepository;
import com.auracart.catalog.repository.attributevalue.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttributeValueService {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final AttributeValueMapper attributeValueMapper;

    @Transactional
    public AttributeValue createAttributeValue(CreateAttributeValueCommand command) {
        validateCommand(command);

        // Referans edilen attribute definition'ın var olması ve değerin bu tanım için
        // benzersiz olması birer iş kuralıdır, validate metodunda değil burada kontrol edilir.
        if (!attributeDefinitionRepository.existsById(command.attributeDefinitionId())) {
            throw new IllegalStateException("Attribute definition bulunamadı: " + command.attributeDefinitionId());
        }
        if (attributeValueRepository.existsByAttributeDefinitionIdAndValueIgnoreCase(command.attributeDefinitionId(), command.value())) {
            throw new IllegalStateException("Bu değer bu attribute definition için zaten mevcut: " + command.value());
        }

        var attributeValue = attributeValueMapper.toEntity(command);
        return attributeValueRepository.save(attributeValue);
    }

    @Transactional
    public List<AttributeValue> createAttributeValues(List<CreateAttributeValueCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            throw new IllegalArgumentException("Attribute value listesi boş olamaz");
        }

        return commands.stream()
                .map(this::createAttributeValue)
                .toList();
    }

    public AttributeValue getAttributeValue(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Attribute value ID boş olamaz");
        }
        return attributeValueRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Attribute value bulunamadı: " + id));
    }

    public List<AttributeValue> getAttributeValuesByDefinition(UUID attributeDefinitionId) {
        if (attributeDefinitionId == null) {
            throw new IllegalArgumentException("Attribute definition ID boş olamaz");
        }
        return attributeValueRepository.findByAttributeDefinitionId(attributeDefinitionId);
    }

    private void validateCommand(CreateAttributeValueCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Attribute value oluşturma komutu boş olamaz");
        }
        if (command.attributeDefinitionId() == null) {
            throw new IllegalArgumentException("Attribute definition ID boş olamaz");
        }
        if (!StringUtils.hasText(command.value())) {
            throw new IllegalArgumentException("Attribute değeri boş olamaz");
        }
    }
}


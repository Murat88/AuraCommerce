package com.auracart.catalog.mapper.attributedefinition;

import com.auracart.catalog.entity.attributedefinition.AttributeDefinition;
import com.auracart.catalog.service.attributedefinition.CreateAttributeDefinitionCommand;
import org.springframework.stereotype.Component;

@Component
public class AttributeDefinitionMapper {

    public AttributeDefinition toEntity(CreateAttributeDefinitionCommand command) {
        var attributeDefinition = new AttributeDefinition();
        attributeDefinition.setName(command.name());
        attributeDefinition.setType(command.type());
        return attributeDefinition;
    }
}


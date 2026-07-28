package com.auracart.catalog.mapper.attributevalue;

import com.auracart.catalog.entity.attributevalue.AttributeValue;
import com.auracart.catalog.service.attributevalue.CreateAttributeValueCommand;
import org.springframework.stereotype.Component;

@Component
public class AttributeValueMapper {

    public AttributeValue toEntity(CreateAttributeValueCommand command) {
        var attributeValue = new AttributeValue();
        attributeValue.setAttributeDefinitionId(command.attributeDefinitionId());
        attributeValue.setValue(command.value());
        return attributeValue;
    }
}


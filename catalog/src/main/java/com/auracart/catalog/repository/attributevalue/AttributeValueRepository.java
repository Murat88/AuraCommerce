package com.auracart.catalog.repository.attributevalue;

import com.auracart.catalog.entity.attributevalue.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * AttributeValue entity için Spring Data JPA repository.
 */
@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, UUID> {

    List<AttributeValue> findByAttributeDefinitionId(UUID attributeDefinitionId);

    boolean existsByAttributeDefinitionIdAndValueIgnoreCase(UUID attributeDefinitionId, String value);
}


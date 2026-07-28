package com.auracart.catalog.repository.attributedefinition;

import com.auracart.catalog.entity.attributedefinition.AttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * AttributeDefinition entity için Spring Data JPA repository.
 */
@Repository
public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, UUID> {

    boolean existsByNameIgnoreCase(String name);
}


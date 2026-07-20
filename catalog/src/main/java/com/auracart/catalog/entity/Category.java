package com.auracart.catalog.entity;

import com.auracart.core.entity.BaseEntity;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLLTreeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;

import java.util.UUID;

/**
 * Kategori (Category) bilgisi. Hiyerarşi PostgreSQL ltree tipi ile "path" kolonunda tutulur.
 * Soft delete için is_active flag'i kullanılır.
 */
@Getter
@Setter
@Entity
@Table(name = "categories")
@SQLRestriction("is_active = true")
public class Category extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "parent_id")
    private UUID parentId;

    @Type(PostgreSQLLTreeType.class)
    @Column(name = "path", columnDefinition = "ltree")
    private String path;

    @Column(name = "is_active")
    private Boolean isActive;
}


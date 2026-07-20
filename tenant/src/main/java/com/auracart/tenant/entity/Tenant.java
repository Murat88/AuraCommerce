package com.auracart.tenant.entity;

import com.auracart.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Master DB'de tutulan tenant (kiracı) bilgisi.
 * Her tenant, kendine ait izole bir veritabanına (db_name) sahiptir.
 */
@Getter
@Setter
@Entity
@Table(name = "tenants",schema = "auracartmaster")
public class Tenant extends BaseEntity  {

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "subdomain", unique = true)
    private String subdomain;

    @Column(name = "custom_domain", unique = true)
    private String customDomain;

    @Column(name = "db_name", unique = true)
    private String dbName;

    @Column(name = "is_active")
    private Boolean isActive;
}


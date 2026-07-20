package com.auracart.tenant.config;

/**
 * Tenant modülünde kullanılan cache isimlerini merkezi olarak tutar.
 * Sabitler doğrudan string kullanımını (magic string) engellemek için tanımlanmıştır.
 */
public final class TenantCacheNames {

    /**
     * Aktif tenant listesinin Redis üzerinde tutulduğu cache adı.
     */
    public static final String TENANTS = "tenants";

    private TenantCacheNames() {
    }
}


package com.auracart.core.database;

import com.auracart.core.context.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Aktif tenant'a göre doğru DataSource'u belirleyen routing bileşeni.
 * Lookup key olarak {@link TenantContext#getTenantId()} değerini kullanır.
 */
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenantId();
    }
}


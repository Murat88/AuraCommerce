package com.auracart.tenant.service;

import com.auracart.tenant.config.TenantCacheNames;
import com.auracart.tenant.entity.Tenant;
import com.auracart.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tenant'lara ilişkin sorgulama ve iş kurallarını barındıran servis.
 * <p>
 * Repository erişimi bu katmanda kapsüllenir; controller/filter gibi
 * web katmanları doğrudan {@link TenantRepository} kullanmamalıdır.
 * <p>
 * Her istekte veritabanına gitmemek için aktif tenant listesi Redis
 * üzerinde {@link TenantCacheNames#TENANTS} adlı cache'te tutulur;
 * subdomain/custom domain çözümlemesi bu cache üzerinden yapılır.
 * Cache, TTL dolduğunda ya da tenant verisi değiştiğinde
 * ({@link #evictTenantCache()}) yenilenir.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantService {

    private final TenantRepository tenantRepository;

    /**
     * Aktif tüm tenant'ların listesini döner. Sonuç Redis'te cache'lenir;
     * böylece her API çağrısında veritabanına gitmek gerekmez. Cache boşsa
     * (ilk çağrı ya da TTL/evict sonrası) veritabanından yüklenir.
     */
    @Cacheable(cacheNames = TenantCacheNames.TENANTS, key = "'all-active'")
    public List<Tenant> findAllActiveTenants() {
        log.debug("Aktif tenant listesi veritabanından yükleniyor (cache miss).");
        return tenantRepository.findByIsActive(true);
    }

    /**
     * Bir tenant için veritabanı/şema kimliğini döner.
     * db_name tanımlı değilse subdomain fallback olarak kullanılır.
     */
    public String resolveTenantIdentifier(Tenant tenant) {
        return tenant.getDbName() != null ? tenant.getDbName() : tenant.getSubdomain();
    }

    /**
     * Redis üzerindeki tenant cache'ini temizler. Bir tenant oluşturulduğunda,
     * güncellendiğinde ya da silindiğinde, sonraki isteğin güncel veriyi
     * görebilmesi için bu metot çağrılmalıdır.
     */
    @CacheEvict(cacheNames = TenantCacheNames.TENANTS, allEntries = true)
    public void evictTenantCache() {
        log.info("Tenant cache temizlendi.");
    }
}



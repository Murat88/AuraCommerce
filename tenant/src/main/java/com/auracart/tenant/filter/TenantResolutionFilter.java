package com.auracart.tenant.filter;

import com.auracart.core.context.TenantContext;
import com.auracart.tenant.entity.Tenant;
import com.auracart.tenant.service.TenantService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * HTTP isteğinin host bilgisinden (subdomain) tenant'ı çözümleyip
 * {@link TenantContext} içine yerleştiren filtre.
 * <p>
 * Modular Monolith mimarisinde tenant çözümleme sorumluluğu artık
 * tamamen bu modüldedir; `core-shared` sadece ThreadLocal taşıyıcısını (TenantContext) sağlar.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantResolutionFilter extends OncePerRequestFilter {

    private static final String LOCALHOST_SUFFIX = ".localhost";
    private static final String DOMAIN_SUFFIX = ".yourdomain.com";

    private final TenantService tenantService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String host = request.getServerName();
            String subdomain = extractSubdomain(host);

            if (subdomain != null && !subdomain.isBlank()) {
                var allTenants = tenantService.findAllActiveTenants();
                var tenantOpt = allTenants.stream()
                        .filter(tenant -> subdomain.equalsIgnoreCase(tenant.getSubdomain()))
                        .findFirst();
                if (tenantOpt.isEmpty()) {
                    // Tenant bulunamadı ya da aktif değil: isteği hemen durdur, zinciri asla devam ettirme.
                    log.debug("Subdomain '{}' için aktif tenant bulunamadı, istek reddediliyor.", subdomain);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tenant not found or inactive");
                    return;
                }

                Tenant tenant = tenantOpt.get();
                String tenantIdentifier = tenantService.resolveTenantIdentifier(tenant);
                log.debug("Subdomain '{}' için tenant çözümlendi: {}", subdomain, tenantIdentifier);
                TenantContext.setTenantId(tenantIdentifier);
            } else {
                TenantContext.setTenantId(TenantContext.DEFAULT_TENANT_ID);
            }

            filterChain.doFilter(request, response);
        } finally {
            // ThreadLocal bellek sızıntısını önlemek için mutlaka temizle.
            TenantContext.clear();
        }
    }

    /**
     * Host adından subdomain bilgisini çıkarır.
     * Örnek: "acme.localhost" -> "acme", "acme.yourdomain.com" -> "acme".
     */
    private String extractSubdomain(String host) {
        if (host == null || host.isBlank()) {
            return null;
        }

        if (host.endsWith(LOCALHOST_SUFFIX)) {
            return host.substring(0, host.length() - LOCALHOST_SUFFIX.length());
        }

        if (host.endsWith(DOMAIN_SUFFIX)) {
            return host.substring(0, host.length() - DOMAIN_SUFFIX.length());
        }

        String[] parts = host.split("\\.");
        if (parts.length > 2) {
            return parts[0];
        }

        return null;
    }
}


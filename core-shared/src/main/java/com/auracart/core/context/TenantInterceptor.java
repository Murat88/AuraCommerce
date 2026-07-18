package com.auracart.core.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId != null && !tenantId.isBlank()) {
            TenantContext.setTenantId(tenantId);
        } else {
            // İsteğe bağlı: Eğer tenant id zorunluysa burada hata fırlatabilirsin (örneğin 400 Bad Request).
            // Şimdilik varsayılan tenant üzerinden devam ediyoruz.
            TenantContext.setTenantId(TenantContext.DEFAULT_TENANT_ID);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        // İstek bittiğinde memory leak (bellek sızıntısı) olmaması için ThreadLocal'i mutlaka temizlemeliyiz.
        TenantContext.clear();
    }
}
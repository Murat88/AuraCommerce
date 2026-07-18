package com.auracart.core.config;

import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // TODO: Spring Security eklendiğinde burası güncellenecek.
        // Örnek: return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());

        return Optional.of("system");
    }
}
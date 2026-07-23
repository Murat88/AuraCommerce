package com.auracart.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4.1 ile birlikte {@code spring-boot-starter-web}, klasik
 * {@code com.fasterxml.jackson.databind.ObjectMapper} yerine yeni Jackson 3
 * ({@code tools.jackson.databind.ObjectMapper}) auto-configuration'ını
 * kullanmaya başladı. Bu yüzden {@link com.fasterxml.jackson.databind.ObjectMapper}
 * tipinde otomatik bir bean artık oluşturulmuyor.
 * <p>
 * {@link com.auracart.catalog.outbox.OutboxEventListener} gibi hâlâ klasik
 * Jackson 2 API'sine bağımlı sınıflar için burada elle bir
 * {@link ObjectMapper} bean'i tanımlıyoruz.
 */
@Configuration
public class JacksonConfig {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}


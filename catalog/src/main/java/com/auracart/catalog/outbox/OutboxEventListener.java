package com.auracart.catalog.outbox;

import com.auracart.catalog.service.ProductCreatedEvent;
import com.auracart.core.outbox.OutboxEvent;
import com.auracart.core.outbox.OutboxEventRepository;
import com.auracart.core.outbox.OutboxStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * {@link ProductCreatedEvent} olayını yakalayıp {@code outbox_events}
 * tablosuna yazan listener.
 * <p>
 * NOT: Bu sınıf, {@link ProductCreatedEvent}'e derleme zamanında erişim
 * gerektirdiği için {@code catalog} modülünde tanımlanmıştır (core-shared'da
 * DEĞİL). core-shared'ın catalog modülüne bağımlı olması, catalog zaten
 * core-shared'a bağımlı olduğundan döngüsel bir Maven bağımlılığı
 * oluşturacağı ve "core modülünde iş mantığı olmaz" kuralını ihlal edeceği
 * için bu tercih edilmiştir. Outbox altyapısı (OutboxEvent, OutboxStatus,
 * OutboxEventRepository) core-shared'da kalmaya devam eder ve bu sınıf
 * onu kullanır.
 * <p>
 * {@link TransactionalEventListener} ile {@code BEFORE_COMMIT} fazında
 * çalışır; böylece outbox kaydı, ürün oluşturma işlemiyle AYNI veritabanı
 * transaction'ı içinde eklenir (Outbox Pattern'in temel garantisi).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private static final String CATALOG_AGGREGATE_TYPE = "CATALOG";
    private static final String PRODUCT_CREATED_EVENT_TYPE = "PRODUCT_CREATED";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onProductCreated(ProductCreatedEvent event) {
        try {
            String payloadJson = objectMapper.writeValueAsString(event.payload());

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType(CATALOG_AGGREGATE_TYPE)
                    .aggregateId(event.productId())
                    .eventType(PRODUCT_CREATED_EVENT_TYPE)
                    .payload(payloadJson)
                    .status(OutboxStatus.PENDING)
                    .build();

            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            log.error("ProductCreatedEvent payload'u JSON'a serileştirilemedi. productId={}",
                    event.productId(), e);
            throw new IllegalStateException(
                    "Failed to serialize ProductCreatedEvent payload for outbox", e);
        }
    }
}


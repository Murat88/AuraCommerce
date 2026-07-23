package com.auracart.catalog.service;

import java.util.UUID;

/**
 * Bir ürün başarıyla oluşturulduğunda yayınlanan uygulama içi olay (event).
 * <p>
 * Bu olay {@code @Transactional} bir metot içinde yayınlandığı için, aynı DB
 * transaction'ı içinde işlenir. Core modülündeki bir listener bu olayı
 * yakalayarak {@code outbox_events} tablosuna aşağıdaki bilgilerle bir kayıt
 * yazacaktır:
 * <ul>
 *     <li>aggregateType = "CATALOG"</li>
 *     <li>aggregateId = {@link #productId()}</li>
 *     <li>eventType = "PRODUCT_CREATED"</li>
 *     <li>payload = {@link #payload()}</li>
 * </ul>
 */
public record ProductCreatedEvent(
        UUID productId,
        Object payload
) {
}


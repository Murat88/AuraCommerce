package com.auracart.core.outbox;

/**
 * {@code outbox_events} tablosundaki bir olayın işlenme durumunu temsil eder.
 */
public enum OutboxStatus {

    /**
     * Olay kaydedildi ancak henüz worker tarafından dış sisteme yayınlanmadı.
     */
    PENDING,

    /**
     * Olay başarıyla dış sisteme (mesaj kuyruğu vb.) yayınlandı.
     */
    PUBLISHED,

    /**
     * Olayın yayınlanması denendi ancak başarısız oldu.
     */
    FAILED
}


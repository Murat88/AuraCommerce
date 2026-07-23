package com.auracart.core.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * {@code outbox_events} tablosuna karşılık gelen JPA Entity'si.
 * <p>
 * Outbox Pattern'i uygular: bir domain işlemi (örn. ürün oluşturma) ile
 * birlikte aynı veritabanı transaction'ı içinde bu tabloya bir kayıt atılır.
 * Ayrı bir worker (bu görevin kapsamı dışında) bu tabloyu periyodik olarak
 * okuyup {@code PENDING} durumundaki olayları dış sistemlere yayınlar.
 * <p>
 * {@code payload} alanı, Hibernate'in native {@code @JdbcTypeCode(SqlTypes.JSON)}
 * desteği ile {@code jsonb} kolonuna map edilir. NOT: Bu proje Hibernate
 * 7.4 kullandığından, mevcut en güncel Hypersistence Utils sürümü
 * ({@code hypersistence-utils-hibernate-63}) {@code org.hibernate.query.BindableType}
 * sınıfının kaldırılmış/taşınmış olması nedeniyle derleme zamanında
 * uyumsuzluk oluşturuyor; bu yüzden bu alan için Hibernate'in kendi native
 * JSON tip desteği tercih edilmiştir (aynı jsonb sonucunu verir).
 * <p>
 * {@link com.auracart.core.entity.BaseEntity}'yi extend ETMEZ; çünkü
 * BaseEntity'nin sahip olduğu {@code created_by}, {@code updated_at},
 * {@code updated_by} ve {@code version} alanları DBML'deki
 * {@code outbox_events} tablosunda bulunmamaktadır. Bu entity, DBML şemasını
 * birebir yansıtır.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OutboxStatus.PENDING;
        }
    }
}





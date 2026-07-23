package com.auracart.core.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * {@link OutboxEvent} kayıtları için temel CRUD operasyonlarını sağlayan
 * Spring Data repository.
 * <p>
 * NOT: {@code PENDING} durumundaki kayıtları okuyup işleyecek worker/scheduler
 * bu görevin kapsamı dışındadır ve ayrıca eklenecektir.
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}


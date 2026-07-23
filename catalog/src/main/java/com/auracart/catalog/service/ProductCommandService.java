package com.auracart.catalog.service;

import com.auracart.catalog.entity.Product;
import com.auracart.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ürün oluşturma gibi karmaşık, ACID uyumlu yazma (command) operasyonlarını
 * yöneten servis. Outbox Pattern entegrasyonu, Spring Application Events
 * kullanılarak aynı veritabanı transaction'ı içinde sağlanır.
 */
@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Yeni bir ürünü (ve teorik olarak varyant/medya kayıtlarını, cascade
     * ilişkileri üzerinden) tek bir transaction içinde oluşturur ve ardından
     * {@link ProductCreatedEvent} olayını yayınlar.
     * <p>
     * Metot {@code @Transactional} olduğu için, olay yayınlandığında henüz
     * transaction commit edilmemiş olur; core modülündeki outbox listener'ı
     * bu olayı aynı transaction içinde yakalayıp {@code outbox_events}
     * tablosuna yazacaktır.
     *
     * @param command ürün oluşturma için gerekli girdi verileri
     * @return oluşturulan {@link Product} kaydı
     */
    @Transactional
    public Product createProduct(CreateProductCommand command) {
        Product product = new Product();
        product.setCategoryId(command.categoryId());
        product.setBrandId(command.brandId());
        product.setName(command.name());
        product.setSlug(command.slug());
        product.setDescription(command.description());
        product.setProductType(command.productType());
        product.setStatus(command.status());

        // NOT: Varyant ve medya kayıtları için ayrı entity/repository'ler
        // eklendiğinde, burada aynı transaction içinde cascade ile
        // kaydedilmeleri beklenir.
        Product savedProduct = productRepository.save(product);

        ProductCreatedEvent event = new ProductCreatedEvent(savedProduct.getId(), savedProduct);
        eventPublisher.publishEvent(event);

        return savedProduct;
    }
}


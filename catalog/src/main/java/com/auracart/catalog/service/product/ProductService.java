package com.auracart.catalog.service.product;

import com.auracart.catalog.entity.product.Product;
import com.auracart.catalog.mapper.product.ProductMapper;
import com.auracart.catalog.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Product createProduct(CreateProductCommand command) {
        validateCommand(command);

        // Slug benzersizliği bir iş kuralıdır, validate metodunda değil burada kontrol edilir.
        if (productRepository.existsBySlug(command.slug())) {
            throw new IllegalStateException("Bu slug zaten kullanılıyor: " + command.slug());
        }

        var product = productMapper.toEntity(command);
        var savedProduct = productRepository.save(product);

        // Event, transaction commit edilmeden önce yayınlanır; outbox listener'ı
        // aynı transaction içinde outbox_events tablosuna yazar.
        var event = new ProductCreatedEvent(savedProduct.getId(), savedProduct);
        eventPublisher.publishEvent(event);

        return savedProduct;
    }

    private void validateCommand(CreateProductCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Ürün oluşturma komutu boş olamaz");
        }
        if (command.categoryId() == null) {
            throw new IllegalArgumentException("Kategori ID boş olamaz");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new IllegalArgumentException("Ürün adı boş olamaz");
        }
        if (!StringUtils.hasText(command.slug())) {
            throw new IllegalArgumentException("Slug boş olamaz");
        }
        if (command.productType() == null) {
            throw new IllegalArgumentException("Ürün tipi boş olamaz");
        }
        if (command.status() == null) {
            throw new IllegalArgumentException("Ürün durumu boş olamaz");
        }
    }
}


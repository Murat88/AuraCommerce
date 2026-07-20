package com.auracart.core.database;

import com.auracart.core.database.properties.TenantDataSourceProperties;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Boot DataSource ve JPA yapılandırması.
 * <p>
 * Master DB, tenant meta verilerini (tenants, tenant_features) tutar.
 * Gerçek operasyonel sorgular ise {@link TenantRoutingDataSource} aracılığıyla
 * aktif tenant'a ait veritabanına yönlendirilir.
 * <p>
 * NOT: Tenant DataSource'larının Master DB'den dinamik olarak yüklenmesi
 * henüz implemente edilmedi; bu, ilerleyen bir adımda eklenecek.
 */
@Configuration
public class DataSourceConfig {

    /**
     * Master (yönetim) veritabanı için DataSource.
     * Sadece `tenants` ve `tenant_features` tablolarını barındırır.
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return new DriverManagerDataSource();
    }

    /**
     * Aktif tenant'a göre doğru veritabanına yönlendirme yapan router DataSource.
     * Uygulamanın kullanacağı birincil (primary) DataSource budur.
     */
//    @Bean
//    @Primary
//    public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource) {
//        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
//        routingDataSource.setDefaultTargetDataSource(masterDataSource);
//
//        Map<Object, Object> targetDataSources = new HashMap<>();
//
//        // --- POC İÇİN GEÇİCİ EKLENTİ BAŞLANGICI ---
//        DriverManagerDataSource auracommerceDataSource1 = new DriverManagerDataSource();
//        // Kendi veritabanı ayarlarına göre buraları güncelle
//        auracommerceDataSource1.setDriverClassName("org.postgresql.Driver");
//        auracommerceDataSource1.setUrl("jdbc:postgresql://localhost:5432/auracommerce1");
//        auracommerceDataSource1.setUsername("postgres");
//        auracommerceDataSource1.setPassword("976034");
//
//        targetDataSources.put("auracommerce1", auracommerceDataSource1);
//
//        // --- POC İÇİN GEÇİCİ EKLENTİ BAŞLANGICI ---
//        DriverManagerDataSource auracommerceDataSource2 = new DriverManagerDataSource();
//        // Kendi veritabanı ayarlarına göre buraları güncelle
//        auracommerceDataSource2.setDriverClassName("org.postgresql.Driver");
//        auracommerceDataSource2.setUrl("jdbc:postgresql://localhost:5432/auracommerce2");
//        auracommerceDataSource2.setUsername("postgres");
//        auracommerceDataSource2.setPassword("976034");
//
//        // Filter'dan dönen db_name ("auracommerce") anahtar kelime olmalı
//        targetDataSources.put("auracommerce2", auracommerceDataSource2);
//        // --- POC İÇİN GEÇİCİ EKLENTİ BİTİŞİ ---
//
//        routingDataSource.setTargetDataSources(targetDataSources);
//        routingDataSource.afterPropertiesSet();
//        return routingDataSource;
//    }

    @Bean
    @Primary
    public DataSource routingDataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            TenantDataSourceProperties tenantProps,
            DataSourceFactory factory) {

        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        Map<Object, Object> targetDataSources = new HashMap<>();
        tenantProps.getDatasources().forEach((tenantId, props) ->
                targetDataSources.put(tenantId, factory.create(props)));

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }


    /**
     * Routing DataSource üzerinden çalışan EntityManagerFactory.
     */
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("routingDataSource") DataSource routingDataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(routingDataSource);
        emf.setPackagesToScan("com.auracart.core", "com.auracart");
        emf.setPersistenceUnitName("default");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        return emf;
    }

    /**
     * EntityManagerFactory'ye bağlı transaction manager.
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}



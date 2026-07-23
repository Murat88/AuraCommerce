package com.auracart.catalog.repository;

import com.auracart.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Category entity için Spring Data JPA repository.
 * Hiyerarşik sorgular PostgreSQL'in ltree tipi kullanılarak native query ile yapılır.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Verilen path altındaki tüm alt kategorileri (descendants) getirir.
     * "<@" operatörü ltree hiyerarşisinde "path, verilen path'in altındadır" anlamına gelir.
     * Kendi kaydını sonuçtan hariç tutmak için categoryId ile karşılaştırma yapılır.
     */
    @Query(value = "SELECT * FROM categories WHERE path <@ CAST(:path AS ltree) AND id != CAST(:categoryId AS uuid)", nativeQuery = true)
    List<Category> findAllDescendants(@Param("path") String path, @Param("categoryId") UUID categoryId);
}


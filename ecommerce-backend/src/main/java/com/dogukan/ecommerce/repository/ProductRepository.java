package com.dogukan.ecommerce.repository;

import com.dogukan.ecommerce.dto.response.ProductResponse;
import com.dogukan.ecommerce.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids ORDER BY p.id ASC")
    List<Product> findAllByIdInWithLockOrderByIdAsc(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :productId")
    int restoreStockBulk(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    Page<Product> findByCategory_Slug(String slug, Pageable pageable);

    @Query("SELECT new com.dogukan.ecommerce.dto.response.ProductResponse(" +
            "p.id, p.name, p.description, p.price, p.stock, c.name, c.slug, " +
            "COALESCE(AVG(r.rating), 0.0), COUNT(r.id)) " +
            "FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN Review r ON r.product.id = p.id " +
            "GROUP BY p.id, c.name, c.slug")
    Page<ProductResponse> findAllWithRatings(Pageable pageable);

    @Query("SELECT new com.dogukan.ecommerce.dto.response.ProductResponse(" +
            "p.id, p.name, p.description, p.price, p.stock, c.name, c.slug, " +
            "COALESCE(AVG(r.rating), 0.0), COUNT(r.id)) " +
            "FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN Review r ON r.product.id = p.id " +
            "WHERE c.slug = :categorySlug " +
            "GROUP BY p.id, c.name, c.slug")
    Page<ProductResponse> findAllByCategorySlugWithRatings(@Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("SELECT new com.dogukan.ecommerce.dto.response.ProductResponse(" +
            "p.id, p.name, p.description, p.price, p.stock, c.name, c.slug, " +
            "COALESCE(AVG(r.rating), 0.0), COUNT(r.id)) " +
            "FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN Review r ON r.product.id = p.id " +
            "WHERE p.id = :id " +
            "GROUP BY p.id, c.name, c.slug")
    Optional<ProductResponse> findProductByIdWithRatings(@Param("id") Long id);
}

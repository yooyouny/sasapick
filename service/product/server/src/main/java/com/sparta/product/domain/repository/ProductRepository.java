package com.sparta.product.domain.repository;

import com.sparta.product.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findByProductIdAndIsDeletedFalse(Long productId);

  @Query(
      "SELECT p FROM Product p WHERE "
          + "(:categoryId IS NULL OR p.categoryId = :categoryId) AND "
          + "(:brandName IS NULL OR p.brandName = :brandName) AND "
          + "(:minPrice IS NULL OR p.originalPrice >= :minPrice) AND "
          + "(:maxPrice IS NULL OR p.originalPrice <= :maxPrice) AND "
          + "(:size IS NULL OR p.size = :size) AND "
          + "(:mainColor IS NULL OR p.mainColor = :mainColor) AND "
          + "p.isDeleted = false")
  List<Product> findAllByFilters(
      @Param("categoryId") Long categoryId,
      @Param("brandName") String brandName,
      @Param("minPrice") BigDecimal minPrice,
      @Param("maxPrice") BigDecimal maxPrice,
      @Param("size") String size,
      @Param("mainColor") String mainColor,
      Pageable pageable);
}

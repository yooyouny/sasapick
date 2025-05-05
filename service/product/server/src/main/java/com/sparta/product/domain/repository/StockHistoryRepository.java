package com.sparta.product.domain.repository;

import com.sparta.product.domain.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    
    List<StockHistory> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    List<StockHistory> findByProductIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long productId, LocalDateTime from, LocalDateTime to);
}

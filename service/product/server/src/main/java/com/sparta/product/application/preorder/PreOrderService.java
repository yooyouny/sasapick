package com.sparta.product.application.preorder;

import com.sparta.product.application.dto.StockDto;
import com.sparta.product.domain.model.PreOrder;
import com.sparta.product.domain.model.PreOrderState;
import com.sparta.product.domain.model.Product;
import com.sparta.product.domain.repository.PreOrderRepository;
import com.sparta.product.domain.repository.ProductRepository;
import com.sparta.product.domain.repository.StockRepository;
import com.sparta.product.presentation.exception.ProductErrorCode;
import com.sparta.product.presentation.exception.ProductServerException;
import com.sparta.product.presentation.request.PreOrderCreateRequest;
import com.sparta.product.presentation.request.PreOrderUpdateRequest;
import com.sparta.product.presentation.response.PreOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreOrderService {
  private final PreOrderRepository preOrderRepository;
  private final ProductRepository productRepository;
  private final StockRepository stockRepository;

  public Long createPreOrder(PreOrderCreateRequest request) {
    PreOrder preOrder = PreOrderMapper.toEntity(request);
    PreOrder savedPreOrder = preOrderRepository.save(preOrder);
    return savedPreOrder.getPreOrderId();
  }

  @CacheEvict(cacheNames = "preOrder", key = "#request.preOrderId()")
  public PreOrderResponse updatePreOrder(PreOrderUpdateRequest request) {
    PreOrder preOrder = findPreOrderByPreOrderId(request.preOrderId());
    if (preOrder.getPreOrderId() != request.productId()) {
      Integer quantity = stockRepository.getStock(request.productId());
      validateStock(quantity, request.availableQuantity());
    }
    preOrder.update(
        request.productId(),
        request.preOrderTitle(),
        request.startDateTime(),
        request.endDateTime(),
        request.releaseDateTime(),
        request.availableQuantity());
    return PreOrderResponse.of(preOrder);
  }

  @Transactional(readOnly = true)
  public PreOrderResponse getPreOrder(Long preOrderId) {
    return PreOrderResponse.of(findPreOrderByPreOrderId(preOrderId));
  }

  @Transactional(readOnly = true)
  public Page<PreOrderResponse> getPreOrderList(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return preOrderRepository.findAllByIsPublicTrue(pageable).map(PreOrderResponse::of);
  }

  @CacheEvict(cacheNames = "preOrder", key = "#preOrderId")
  public PreOrderResponse updateState(Long preOrderId, PreOrderState state) {
    PreOrder preOrder = findPreOrderByPreOrderId(preOrderId);
    if (state == PreOrderState.OPEN_FOR_ORDER) preOrder.open();
    else if (state == PreOrderState.CANCELLED) preOrder.cancel(); // TODO :: 해당 주문건들 sate 전파필요
    return PreOrderResponse.of(preOrder);
  }

  @CacheEvict(cacheNames = "preOrder", key = "#preOrderId")
  public void deletePreOrder(Long preOrderId) {
    PreOrder preOrder = findPreOrderByPreOrderId(preOrderId);
    preOrderRepository.delete(preOrder);
  }

  @Transactional(readOnly = true)
  public PreOrder findPreOrderByPreOrderId(Long preOrderId) {
    return preOrderRepository
        .findByPreOrderIdAndIsPublicTrue(preOrderId)
        .orElseThrow(() -> new ProductServerException(ProductErrorCode.NOT_FOUND_PREORDER));
  }

  private void validateStock(int nowQuantity, int requestStock) {
    if (nowQuantity <= requestStock)
      throw new ProductServerException(ProductErrorCode.PREORDER_QUANTITY_CONFLICT);
  }
}

package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.PurchaseOrderDto;
import com.example.Shoe_shop.dto.request.PurchaseOrderItemDto;
import com.example.Shoe_shop.entity.ProductVariant;
import com.example.Shoe_shop.entity.PurchaseOrder;
import com.example.Shoe_shop.entity.PurchaseOrderItem;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.PurchaseOrderMapper;
import com.example.Shoe_shop.repository.PurchaseOrderRepository;
import com.example.Shoe_shop.service.PurchaseOrderService;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    final PurchaseOrderRepository purchaseOrderRepository;
    final EntityValidatorUtil entityValidatorUtil;
    final PurchaseOrderMapper mapper;

    @Override
    @Transactional
    public PurchaseOrderDto create(PurchaseOrderDto dto) {
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierName(dto.getSupplierName());
        order.setOrderDate(LocalDateTime.now());

        BigDecimal totalCost = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>();

        for (PurchaseOrderItemDto itemDto : dto.getItems()) {
            ProductVariant variant = entityValidatorUtil.requireProductVariant(itemDto.getVariantId());

            PurchaseOrderItem item =PurchaseOrderItem.builder()
                    .purchaseOrder(order)
                    .variant(variant)
                    .quantity(itemDto.getQuantity())
                    .remainingQty(itemDto.getQuantity())
                    .unitCost(itemDto.getUnitCost())
                    .build();

            totalCost = totalCost.add(item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity())));
            items.add(item);
        }

        order.setItems(items);
        order.setTotalCost(totalCost);

        PurchaseOrder saved = purchaseOrderRepository.save(order);
        return mapper.toDto(saved);
    }

    @Override
    public List<PurchaseOrderDto> getAll() {
        return purchaseOrderRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public PurchaseOrderDto getById(Long id) {
        return purchaseOrderRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND));
    }

    @Override
    @Transactional
    public PurchaseOrderDto update(Long id, PurchaseOrderDto dto) {
        PurchaseOrder order = entityValidatorUtil.requirePurchaseOrder(id);

        order.setSupplierName(dto.getSupplierName());
        // xoá items cũ → thêm lại items mới
        order.getItems().clear();

        BigDecimal totalCost = BigDecimal.ZERO;
        for (PurchaseOrderItemDto itemDto : dto.getItems()) {
            ProductVariant variant = entityValidatorUtil.requireProductVariant(itemDto.getVariantId());

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(order);
            item.setVariant(variant);
            item.setQuantity(itemDto.getQuantity());
            item.setRemainingQty(itemDto.getRemainingQty() != null ? itemDto.getRemainingQty() : itemDto.getQuantity());
            item.setUnitCost(itemDto.getUnitCost());

            totalCost = totalCost.add(item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity())));
            order.getItems().add(item);
        }
        order.setTotalCost(totalCost);

        return mapper.toDto(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!purchaseOrderRepository.existsById(id)) {
            throw new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND);
        }
        purchaseOrderRepository.deleteById(id);
    }
}

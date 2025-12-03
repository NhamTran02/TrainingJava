package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.PurchaseOrderDto;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDto create(PurchaseOrderDto dto);
    List<PurchaseOrderDto> getAll();
    PurchaseOrderDto getById(Long id);
    PurchaseOrderDto update(Long id,PurchaseOrderDto dto);
    void delete(Long id);
}

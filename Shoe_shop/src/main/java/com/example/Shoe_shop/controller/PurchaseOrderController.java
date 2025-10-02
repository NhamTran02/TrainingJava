package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.PurchaseOrderDto;
import com.example.Shoe_shop.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {
    private final PurchaseOrderService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PurchaseOrderDto create(@RequestBody PurchaseOrderDto dto) {
        return service.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PurchaseOrderDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PurchaseOrderDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PurchaseOrderDto update(@PathVariable Long id, @RequestBody PurchaseOrderDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}


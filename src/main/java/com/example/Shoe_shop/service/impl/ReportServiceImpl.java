package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.entity.Order;
import com.example.Shoe_shop.entity.PurchaseOrder;
import com.example.Shoe_shop.repository.OrderRepository;
import com.example.Shoe_shop.repository.PurchaseOrderRepository;
import com.example.Shoe_shop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final PurchaseOrderRepository purchaseOrderRepo;
    private final OrderRepository orderRepo;

    // Tổng giá trị nhập hàng trong khoảng thời gian
    @Override
    public BigDecimal getTotalImported(LocalDateTime from, LocalDateTime to) {
        return purchaseOrderRepo.findAll().stream()
                .filter(po -> !po.getOrderDate().isBefore(from) && !po.getOrderDate().isAfter(to))
                .map(PurchaseOrder::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Tổng doanh thu bán hàng trong khoảng thời gian
    @Override
    public BigDecimal getTotalSold(LocalDateTime from, LocalDateTime to) {
        return orderRepo.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(from) && !o.getCreatedAt().isAfter(to))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //Tổng giá vốn (cost) của các đơn bán ra
    @Override
    public BigDecimal getTotalCostOfOrders(LocalDateTime from, LocalDateTime to) {
        return orderRepo.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(from) && !o.getCreatedAt().isAfter(to))
                .map(Order::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Báo cáo doanh thu
    @Override
    public Map<String, BigDecimal> getRevenue(LocalDateTime from, LocalDateTime to) {
        BigDecimal imported = getTotalImported(from, to);
        BigDecimal sold = getTotalSold(from, to);
        BigDecimal soldCost = getTotalCostOfOrders(from, to);
        BigDecimal profit = sold.subtract(soldCost);
        return Map.of(
                "totalImported", imported,
                "totalSold", sold,
                "totalSoldCost", soldCost,
                "profit", profit
        );
    }
}

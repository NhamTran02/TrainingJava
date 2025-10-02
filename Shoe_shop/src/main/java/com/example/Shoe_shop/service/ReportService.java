package com.example.Shoe_shop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface ReportService {
    BigDecimal getTotalImported(LocalDateTime from, LocalDateTime to);
    BigDecimal getTotalSold(LocalDateTime from, LocalDateTime to);
    BigDecimal getTotalCostOfOrders(LocalDateTime from, LocalDateTime to);
    Map<String, BigDecimal> getRevenue(LocalDateTime from, LocalDateTime to);
}

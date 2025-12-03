package com.example.Shoe_shop.scheduler;

import com.example.Shoe_shop.dto.response.ProductQueueItem;
import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.repository.ProductRepository;
import com.example.Shoe_shop.service.RedisCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Component
public class ProductRelatedProcess {
    final ProductRepository productRepository;
    final RedisCacheService  redisCacheService;
    static final String QUEUE_KEY = "product:queue";
    static final String RELATED_CACHE_PREFIX = "related:product:";

    @Scheduled(fixedRate = Long.MAX_VALUE)
    public void processQueue() {
//        try {
//            int maxRounds = 50;
//            for (int i = 0; i < maxRounds; i++) {
//                ProductQueueItem queueItem = redisCacheService.rPop(QUEUE_KEY, ProductQueueItem.class);
//                if (queueItem == null) break;
//
//                List<Product> productsInCategory = productRepository.findTop20ByCategory_Id(queueItem.getCategoryId());
//
//                List<Long> relatedIds = productsInCategory.stream()
//                        .map(Product::getId)
//                        .filter(id -> !id.equals(queueItem.getProductId()))
//                        .toList();
//
//                String cacheKey = RELATED_CACHE_PREFIX + queueItem.getProductId();
//                redisCacheService.setValueWithTimeout(cacheKey, relatedIds,10, TimeUnit.MINUTES);
//
//                log.info("Cached {} related products for product {}", relatedIds.size(), queueItem.getProductId());
//            }
//        } catch (Exception e) {
//            log.error("Failed to process product queue", e);
//        }
    }

}

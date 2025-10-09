package com.example.Shoe_shop.scheduler;

import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.repository.ProductRepository;
import com.example.Shoe_shop.service.RedisCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductRelatedProcess {
    final ProductRepository productRepository;
    final RedisCacheService  redisCacheService;
    static final String QUEUE_KEY = "product:queue";
    static final String RELATED_CACHE_PREFIX = "related:product:";

    @Async
    @Scheduled(fixedRate = 5000)
    public void processQueue() {
        try {
            while (true) {
                if (redisCacheService.checkExistsKey(QUEUE_KEY)) {
                    Product product= (Product) redisCacheService.rPop(QUEUE_KEY);

                    List<Product> redisProducts = productRepository.findAllByCategory_Id(product.getCategory().getId());

                    List<Long> relatedIds=redisProducts.stream()
                            .map(Product::getId)
                            .toList();

                    String cacheKey = RELATED_CACHE_PREFIX + product.getId();
                    redisCacheService.setValue(cacheKey,relatedIds);
                }
                else {
                    Thread.sleep(100);
                }
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

package com.example.Shoe_shop.scheduler;

import com.example.Shoe_shop.dto.response.ProductQueueItem;
import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.repository.ProductRepository;
import com.example.Shoe_shop.service.RedisCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCronJob {
    final ProductRepository productRepository;
    final RedisCacheService redisCacheService;
    static final String QUEUE_KEY = "product:queue";
    final ProductRelatedProcess process;

    @Async("productQueueExecutor")
    @EventListener(ApplicationReadyEvent.class)
    public void preloadAtStartup() {
        log.info("Application started — Running initial product queue preload...");
        scheduleProductCronJob();
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void scheduleProductCronJob() {
        log.info("Refreshing product queue...");
        redisCacheService.deleteKey(QUEUE_KEY);
        List<Product> products=productRepository.findAll();

        for(Product product:products){
            if (!product.getDeleted()){
                ProductQueueItem item=ProductQueueItem.builder()
                        .productId(product.getId())
                        .categoryId(product.getCategory().getId())
                        .build();
                redisCacheService.lPush(QUEUE_KEY, item);
            }
        }
        log.info("Loaded {} products into Redis queue", products.size());
        process.processQueue();
    }

}

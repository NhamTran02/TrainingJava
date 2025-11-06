package com.example.Shoe_shop.scheduler;

import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.service.ProductService;
import com.example.Shoe_shop.service.RedisCacheService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Log4j2
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductPreloadJob {
    ProductService productService;
    RedisCacheService redisCacheService;

    static final String BASE_KEY = "product:get-all-product-";
    static final int PAGE_SIZE = 20;

    @Async("productQueueExecutor")
    @EventListener(ApplicationReadyEvent.class)
    public void preloadOnStartup() {
        preloadTopPages();
    }

    @Scheduled(cron = "* */5 * * * *")
    public void preloadTopPages() {
        try {
            for (int page=0;page<2;page++) {
                String key = BASE_KEY + page + "-" + PAGE_SIZE;
                PagedResponse<ProductResponse> response=
                        productService.getAllProduct(page,PAGE_SIZE,"id","ASC");
                redisCacheService.setValueWithTimeout(key,response,10, TimeUnit.MINUTES);
                log.info("Preloaded product page {} into Redis key '{}'", page + 1, key);
                for(ProductResponse product: response.getContent()) {
                    try {
                        String keyProduct="product:product-id-"+product.getId();
                        ProductResponse detail= productService.getProductByIdInternal(product.getId());
                        redisCacheService.setValueWithTimeout(keyProduct,detail,10, TimeUnit.MINUTES);
                        log.info("Preloaded product detail {} into Redis key '{}'", product.getId(), keyProduct);
                    }
                    catch (Exception e) {
                        log.warn("Failed to preload detail for product id {}: {}", product.getId(), e.getMessage());
                    }

                }
            }
            log.info("Finished preloading first 2 product pages into Redis");
        }
        catch (Exception e) {
            log.error("Error preloading product pages: {}", e.getMessage(), e);
        }
    }
}

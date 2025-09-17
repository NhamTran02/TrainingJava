package com.example.Shoe_shop.utils;

import com.example.Shoe_shop.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrackingNumberUtil {
    OrderRepository orderRepo;

    public String generateUnique() {
        String tracking;
        do {
            tracking = "TRK" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        } while (orderRepo.existsByTrackingNumber(tracking));
        return tracking;
    }
}

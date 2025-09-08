package com.example.Shoe_shop.utils;

import com.example.Shoe_shop.entity.*;
import com.example.Shoe_shop.repository.*;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    public DataLoader(RoleRepository roleRepository, UserRepository userRepository,
                      CategoryRepository categoryRepository, BrandRepository brandRepository,
                      ProductRepository productRepository, ProductVariantRepository variantRepository,
                      ProductImageRepository productImageRepository,PurchaseOrderRepository purchaseOrderRepository,
                      PurchaseOrderItemRepository purchaseOrderItemRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = variantRepository;
        this.productImageRepository = productImageRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        Faker faker = new Faker();
//
//        //Roles
//        if (roleRepository.count() == 0) {
//            roleRepository.save(Role.builder().roleName(RoleName.ADMIN).build());
//            roleRepository.save(Role.builder().roleName(RoleName.USER).build());
//        }
//        // ---- Categories ----
//        if (categoryRepository.count() == 0) {
//            categoryRepository.save(Category.builder().name("Sneakers").build());
//            categoryRepository.save(Category.builder().name("Boots").build());
//            categoryRepository.save(Category.builder().name("Sandals").build());
//        }
//
//        // ---- Brands ----
//        if(brandRepository.count() == 0){
//            brandRepository.save(Brand.builder().name("Nike").build());
//            brandRepository.save(Brand.builder().name("Adidas").build());
//            brandRepository.save(Brand.builder().name("Puma").build());
//        }
//
//        // ---- Users ----
//        if(userRepository.count() == 0){
//            for(int i=1;i<=10;i++){
//                User user = new User();
//                user.setUsername(faker.name().username());
//                user.setEmail(faker.internet().emailAddress());
//                user.setFullName(faker.name().fullName());
//                user.setPhoneNumber(faker.numerify("0#########"));
//                user.setAddress(faker.address().fullAddress());
//                user.setPasswordHash("$2a$10$hashedPasswordHere"); // BCrypt hash
//                user.setRole(roleRepository.findById(1L).get());
//                userRepository.save(user);
//            }
//        }
//
//        // ---- Products & Variants ----
//        List<Category> categories = categoryRepository.findAll();
//        List<Brand> brands = brandRepository.findAll();
//
//        if (productRepository.count() == 0) {
//            for(int i=1;i<=50000;i++){
//                Product p = new Product();
//                p.setName(faker.commerce().productName());
//                p.setDescription(faker.lorem().sentence());
//                p.setCategory(categories.get(faker.number().numberBetween(0, categories.size())));
//                p.setBrand(brands.get(faker.number().numberBetween(0, brands.size())));
//                productRepository.save(p);
//
//                // Tạo 2-3 variants cho mỗi product
//                for(int v=1; v<=3; v++){
//                    ProductVariant pv = new ProductVariant();
//                    pv.setProduct(p);
//                    pv.setSize(String.valueOf(38 + v));
//                    pv.setColor(faker.color().name());
//                    pv.setRegularPrice(BigDecimal.valueOf(faker.number().numberBetween(50, 200)));
//                    pv.setSalePrice(BigDecimal.valueOf(faker.number().numberBetween(30, 150)));
//                    pv.setStockQuantity(faker.number().numberBetween(10, 100));
//                    productVariantRepository.save(pv);
//                }
//            }
//        }
//
//        //ProductImage
//        if (productImageRepository.count() == 0) {
//            List<Product> products = productRepository.findAll();
//            for (Product p : products) {
//                int numImages=faker.number().numberBetween(1, 4);
//                for(int i=0;i<numImages;i++){
//                    ProductImage productImage = new ProductImage();
//                    productImage.setProduct(p);
//                    productImage.setUrl(faker.internet().url());
//                    productImage.setIsThumbnail(i==0);
//                    productImageRepository.save(productImage);
//                }
//            }
//        }
//
//        //PurchaseOrder
//        if (purchaseOrderRepository.count() == 0) {
//            for(int i=1;i<=50;i++){
//                purchaseOrderRepository.save(PurchaseOrder.builder()
//                                .supplierName(faker.company().name())
//                                .orderDate(faker.date().past(30, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
//                                .totalCost(BigDecimal.valueOf(0.0))
//                        .build());
//            }
//        }
//        //PurchaseOrderItems
//        if (purchaseOrderItemRepository.count() == 0) {
//            List<PurchaseOrder>  purchaseOrders = purchaseOrderRepository.findAll();
//            List<ProductVariant> productVariants=productVariantRepository.findAll();
//            for (PurchaseOrder p : purchaseOrders) {
//                int itemsCount = faker.number().numberBetween(1, 4);
//                double totalCost = 0.0;
//
//                for (int j = 0; j < itemsCount; j++) {
//                    ProductVariant variant = productVariants.get(faker.number().numberBetween(0, productVariants.size()));
//                    int qty = faker.number().numberBetween(1, 20);
//                    double unitCost = faker.number().numberBetween(20, 100);
//
//                    PurchaseOrderItem item = new PurchaseOrderItem();
//                    item.setPurchaseOrder(p);
//                    item.setVariant(variant);
//                    item.setQuantity(qty);
//                    item.setUnitCost(BigDecimal.valueOf(unitCost));
//                    purchaseOrderItemRepository.save(item);
//
//                    totalCost += qty * unitCost;
//                }
//
//            }
//
//        }
    }
}

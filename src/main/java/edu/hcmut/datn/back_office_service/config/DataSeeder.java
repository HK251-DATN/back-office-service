package edu.hcmut.datn.back_office_service.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import edu.hcmut.datn.back_office_service.dao.Category;
import edu.hcmut.datn.back_office_service.dao.PaymentMethod;
import edu.hcmut.datn.back_office_service.dao.ProductGeneral;
import edu.hcmut.datn.back_office_service.dao.SubSubcategory;
import edu.hcmut.datn.back_office_service.repository.CategoryRepository;
import edu.hcmut.datn.back_office_service.repository.PaymentMethodRepository;
import edu.hcmut.datn.back_office_service.repository.ProductGeneralRepository;
import edu.hcmut.datn.back_office_service.repository.SubSubcategoryRepository;
import edu.hcmut.datn.back_office_service.service.CategoryService;
import edu.hcmut.datn.back_office_service.service.ProductGeneralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private static final String INIT_DATA_FILE = "init_data.json";

    // Default image URL from application.yaml - create() persists it as-is;
    // updateProductMainImage() is what actually publishes ProductGeneralCreatedEvent
    // (the real flow defers publishing until the true image is known).
    private static final String DEFAULT_PRODUCT_IMG_URL =
            "https://pub-0365edd1781141cdb68675969c7cdb87.r2.dev/5fe95e6c-c064-49c4-8712-1c8f54472502.jpg";

    private final CategoryRepository categoryRepository;
    private final SubSubcategoryRepository subSubcategoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductGeneralRepository productGeneralRepository;
    private final CategoryService categoryService;
    private final ProductGeneralService productGeneralService;
    private final ObjectMapper objectMapper;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            // Only seed if database is empty
            if (categoryRepository.count() > 0) {
                log.info("Database already contains data. Skipping seeding.");
                return;
            }

            log.info("Starting database seeding from {}...", INIT_DATA_FILE);

            InitData initData;
            try (InputStream is = new ClassPathResource(INIT_DATA_FILE).getInputStream()) {
                initData = objectMapper.readValue(is, InitData.class);
            }

            Map<Integer, Long> categoryIdByJsonId = seedCategories(initData.getCategories());
            Map<Integer, Long> subSubcategoryIdByJsonId =
                    seedSubSubcategories(initData.getSubSubcategories(), categoryIdByJsonId);
            seedPaymentMethods(initData.getPaymentMethods());
            seedProductGenerals(initData.getProductGenerals(), subSubcategoryIdByJsonId);

            // Users/Buyers/Providers/Employees are not seeded here - they arrive via
            // Kafka from identity-service's own DataSeeder (UserCreatedConsumer,
            // ProviderCreatedConsumer, EmpCreatedConsumer), same as the real registration flows.

            log.info("Database seeding completed successfully!");
        };
    }

    private Map<Integer, Long> seedCategories(List<InitData.CategorySeed> categories) {
        Map<Integer, Long> categoryIdByJsonId = new HashMap<>();

        for (InitData.CategorySeed seed : categories) {
            Long belongToCategory = seed.getBelongToCategoryId() == null
                    ? null
                    : categoryIdByJsonId.get(seed.getBelongToCategoryId());

            if (seed.getBelongToCategoryId() != null && belongToCategory == null) {
                log.warn("Category '{}' references unknown belongToCategoryId={}, skipping",
                        seed.getName(), seed.getBelongToCategoryId());
                continue;
            }

            Category category = new Category();
            category.setName(seed.getName());
            category.setDescription(seed.getDescription());
            category.setDisplayOrder(seed.getDisplayOrder());
            category.setIconUrl(seed.getIconUrl());
            category.setIsSubCategory(seed.getIsSubCategory());
            category.setBelongToCategory(belongToCategory);

            Category saved = categoryService.create(category);
            categoryIdByJsonId.put(seed.getId(), saved.getCategoryId());
        }

        log.info("Seeded {} categories", categoryRepository.count());
        return categoryIdByJsonId;
    }

    private Map<Integer, Long> seedSubSubcategories(List<InitData.SubSubcategorySeed> subSubcategories,
                                                      Map<Integer, Long> categoryIdByJsonId) {
        Map<Integer, Long> subSubcategoryIdByJsonId = new HashMap<>();

        for (InitData.SubSubcategorySeed seed : subSubcategories) {
            Long subcategoryId = categoryIdByJsonId.get(seed.getSubcategoryId());
            if (subcategoryId == null) {
                log.warn("SubSubcategory '{}' references unknown subcategoryId={}, skipping",
                        seed.getName(), seed.getSubcategoryId());
                continue;
            }

            SubSubcategory subSubcategory = new SubSubcategory();
            subSubcategory.setName(seed.getName());
            subSubcategory.setDescription(seed.getDescription());
            subSubcategory.setIconUrl(seed.getIconUrl());
            subSubcategory.setSubcategoryId(subcategoryId);
            subSubcategory.setAvgShelfDays(seed.getAvgShelfDays());

            SubSubcategory saved = categoryService.createSubSubcategory(subSubcategory);
            subSubcategoryIdByJsonId.put(seed.getId(), saved.getSubSubcategoryId());
        }

        log.info("Seeded {} sub-subcategories", subSubcategoryRepository.count());
        return subSubcategoryIdByJsonId;
    }

    private void seedPaymentMethods(List<InitData.PaymentMethodSeed> paymentMethods) {
        for (InitData.PaymentMethodSeed seed : paymentMethods) {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setPaymentType(seed.getPaymentType());
            paymentMethod.setPaymentProvider(seed.getPaymentProvider());
            paymentMethod.setAccountNum(seed.getAccountNum());
            paymentMethod.setIsActive(seed.isActive());
            paymentMethodRepository.save(paymentMethod);
        }

        log.info("Seeded {} payment methods", paymentMethodRepository.count());
    }

    private void seedProductGenerals(List<InitData.ProductGeneralSeed> productGenerals,
                                      Map<Integer, Long> subSubcategoryIdByJsonId) {
        for (InitData.ProductGeneralSeed seed : productGenerals) {
            Long subSubcategoryId = subSubcategoryIdByJsonId.get(seed.getSubSubcategoryId());
            if (subSubcategoryId == null) {
                log.warn("ProductGeneral '{}' references unknown subSubcategoryId={}, skipping",
                        seed.getName(), seed.getSubSubcategoryId());
                continue;
            }

            ProductGeneral product = new ProductGeneral();
            product.setProdName(seed.getName());
            product.setDescription(seed.getDescription());
            product.setTags(seed.getTags());
            product.setUnit(seed.getUnit());
            product.setUnitQuantity(seed.getUnitQuantity());
            product.setSubSubcategoryId(subSubcategoryId);
            product.setImgUrl(DEFAULT_PRODUCT_IMG_URL);

            ProductGeneral saved = productGeneralService.create(product);
            productGeneralService.updateProductMainImage(saved.getProdGenId(), DEFAULT_PRODUCT_IMG_URL);
        }

        log.info("Seeded {} sample fresh food products", productGeneralRepository.count());
    }
}

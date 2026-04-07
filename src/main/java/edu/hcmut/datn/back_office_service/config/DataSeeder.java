package edu.hcmut.datn.back_office_service.config;

import edu.hcmut.datn.back_office_service.common.enums.*;
import edu.hcmut.datn.back_office_service.dao.*;
import edu.hcmut.datn.back_office_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final CategoryRepository categoryRepository;
    private final SubSubcategoryRepository subSubcategoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductGeneralRepository productGeneralRepository;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            // Only seed if database is empty
            if (categoryRepository.count() > 0) {
                log.info("Database already contains data. Skipping seeding.");
                return;
            }

            log.info("Starting database seeding...");

            // Seed Main Categories - Fresh Food Focus (Vietnamese)
            Category fruitsVegetables = createCategory("Trái Cây & Rau Củ", "Trái cây và rau củ tươi sạch", 1, null, "N", null);
            Category meatSeafood = createCategory("Thịt & Hải Sản", "Thịt tươi, gia cầm và hải sản", 2, null, "N", null);
            Category dairyEggs = createCategory("Sữa & Trứng", "Sản phẩm sữa và trứng tươi", 3, null, "N", null);
            Category bakery = createCategory("Bánh Mì & Bánh Ngọt", "Bánh mì và bánh nướng tươi", 4, null, "N", null);
            Category beverages = createCategory("Đồ Uống", "Nước ép tươi và đồ uống", 5, null, "N", null);

            log.info("Seeded {} main categories", categoryRepository.count());

            // Seed Subcategories under Fruits & Vegetables
            Category fruits = createCategory("Trái Cây", "Trái cây tươi theo mùa", 1, null, "Y", fruitsVegetables.getCategoryId());
            Category vegetables = createCategory("Rau Củ", "Rau củ tươi sạch", 2, null, "Y", fruitsVegetables.getCategoryId());
            Category herbs = createCategory("Rau Thơm & Gia Vị", "Rau thơm và gia vị tươi", 3, null, "Y", fruitsVegetables.getCategoryId());

            // Seed Subcategories under Meat & Seafood
            Category poultry = createCategory("Gia Cầm", "Gà, vịt và các loại gia cầm tươi", 1, null, "Y", meatSeafood.getCategoryId());
            Category redMeat = createCategory("Thịt Đỏ", "Thịt bò, heo và cừu tươi", 2, null, "Y", meatSeafood.getCategoryId());
            Category seafood = createCategory("Hải Sản", "Cá và hải sản tươi sống", 3, null, "Y", meatSeafood.getCategoryId());

            // Seed Subcategories under Dairy & Eggs
            Category milkProducts = createCategory("Sản Phẩm Sữa", "Sữa tươi và kem", 1, null, "Y", dairyEggs.getCategoryId());
            Category cheeseProducts = createCategory("Phô Mai", "Phô mai tươi và ủ", 2, null, "Y", dairyEggs.getCategoryId());
            Category eggsProducts = createCategory("Trứng", "Trứng tươi", 3, null, "Y", dairyEggs.getCategoryId());

            // Seed Subcategories under Bakery
            Category bread = createCategory("Bánh Mì", "Bánh mì nướng tươi", 1, null, "Y", bakery.getCategoryId());
            Category pastries = createCategory("Bánh Ngọt", "Bánh ngọt và bánh kem tươi", 2, null, "Y", bakery.getCategoryId());

            // Seed Subcategories under Beverages
            Category freshJuice = createCategory("Nước Ép Tươi", "Nước ép trái cây tươi vắt", 1, null, "Y", beverages.getCategoryId());
            Category plantBasedMilk = createCategory("Sữa Thực Vật", "Sữa hạnh nhân, đậu nành và yến mạch", 2, null, "Y", beverages.getCategoryId());

            log.info("Seeded {} total categories (including subcategories)", categoryRepository.count());

            // Seed SubSubcategories - Fruits
            SubSubcategory tropicalFruits = createSubSubcategory("Trái Cây Nhiệt Đới", "Xoài, dứa, đu đủ", null, fruits.getCategoryId());
            SubSubcategory citrusFruits = createSubSubcategory("Trái Cây Có Múi", "Cam, chanh, quýt", null, fruits.getCategoryId());
            SubSubcategory berries = createSubSubcategory("Quả Mọng", "Dâu tây, việt quất, mâm xôi", null, fruits.getCategoryId());
            SubSubcategory applesPears = createSubSubcategory("Táo & Lê", "Các loại táo và lê", null, fruits.getCategoryId());

            // Seed SubSubcategories - Vegetables
            SubSubcategory leafyGreens = createSubSubcategory("Rau Lá Xanh", "Xà lách, rau bina, cải xoăn", null, vegetables.getCategoryId());
            SubSubcategory rootVegetables = createSubSubcategory("Củ Quả", "Cà rốt, khoai tây, củ cải", null, vegetables.getCategoryId());
            SubSubcategory tomatoesCucumbers = createSubSubcategory("Cà Chua & Dưa Leo", "Cà chua và dưa leo tươi", null, vegetables.getCategoryId());
            SubSubcategory mushrooms = createSubSubcategory("Nấm", "Nấm tươi các loại", null, vegetables.getCategoryId());

            // Seed SubSubcategories - Herbs
            SubSubcategory freshHerbs = createSubSubcategory("Rau Thơm Tươi", "Húng quế, rau mùi, ngò tây", null, herbs.getCategoryId());
            SubSubcategory aromatics = createSubSubcategory("Gia Vị", "Tỏi, hành, gừng", null, herbs.getCategoryId());

            // Seed SubSubcategories - Poultry
            SubSubcategory chicken = createSubSubcategory("Thịt Gà", "Gà tươi nguyên con và từng phần", null, poultry.getCategoryId());
            SubSubcategory duck = createSubSubcategory("Thịt Vịt", "Thịt vịt tươi", null, poultry.getCategoryId());

            // Seed SubSubcategories - Red Meat
            SubSubcategory beef = createSubSubcategory("Thịt Bò", "Các loại thịt bò tươi", null, redMeat.getCategoryId());
            SubSubcategory pork = createSubSubcategory("Thịt Heo", "Các loại thịt heo tươi", null, redMeat.getCategoryId());

            // Seed SubSubcategories - Seafood
            SubSubcategory fish = createSubSubcategory("Cá", "Phi lê cá và cá nguyên con tươi", null, seafood.getCategoryId());
            SubSubcategory shellfish = createSubSubcategory("Hải Sản Có Vỏ", "Tôm, cua, tôm hùm", null, seafood.getCategoryId());

            // Seed SubSubcategories - Milk Products
            SubSubcategory freshMilk = createSubSubcategory("Sữa Tươi", "Sữa nguyên kem, tách béo và ít béo", null, milkProducts.getCategoryId());
            SubSubcategory yogurt = createSubSubcategory("Sữa Chua", "Sữa chua nguyên chất và có hương vị", null, milkProducts.getCategoryId());

            // Seed SubSubcategories - Cheese
            SubSubcategory softCheese = createSubSubcategory("Phô Mai Mềm", "Mozzarella, ricotta, phô mai kem", null, cheeseProducts.getCategoryId());
            SubSubcategory hardCheese = createSubSubcategory("Phô Mai Cứng", "Cheddar, parmesan, gouda", null, cheeseProducts.getCategoryId());

            // Seed SubSubcategories - Eggs
            SubSubcategory chickenEggs = createSubSubcategory("Trứng Gà", "Trứng gà trắng và nâu", null, eggsProducts.getCategoryId());

            // Seed SubSubcategories - Bread
            SubSubcategory wheatBread = createSubSubcategory("Bánh Mì Lúa Mì", "Bánh mì lúa mì nguyên cám và đa곡céréales", null, bread.getCategoryId());
            SubSubcategory whiteBread = createSubSubcategory("Bánh Mì Trắng", "Bánh mì trắng và bánh mì Pháp", null, bread.getCategoryId());

            // Seed SubSubcategories - Pastries
            SubSubcategory croissants = createSubSubcategory("Bánh Sừng Bò & Bánh Ngọt", "Bánh sừng bò và bánh ngọt Đan Mạch tươi", null, pastries.getCategoryId());

            // Seed SubSubcategories - Fresh Juice
            SubSubcategory orangeJuice = createSubSubcategory("Nước Cam", "Nước cam vắt tươi", null, freshJuice.getCategoryId());
            SubSubcategory mixedJuice = createSubSubcategory("Nước Ép Hỗn Hợp", "Hỗn hợp trái cây và rau củ", null, freshJuice.getCategoryId());

            // Seed SubSubcategories - Plant-Based Milk
            SubSubcategory almondMilk = createSubSubcategory("Sữa Hạnh Nhân", "Sữa hạnh nhân tươi", null, plantBasedMilk.getCategoryId());
            SubSubcategory oatMilk = createSubSubcategory("Sữa Yến Mạch", "Sữa yến mạch tươi", null, plantBasedMilk.getCategoryId());

            log.info("Seeded {} sub-subcategories", subSubcategoryRepository.count());

            // Seed Payment Methods
            PaymentMethod momo = createPaymentMethod(PaymentType.E_WALLET, PaymentProvider.MOMO, "MOMO_ACCOUNT", true);
            PaymentMethod zalopay = createPaymentMethod(PaymentType.E_WALLET, PaymentProvider.ZALOPAY, "ZALOPAY_ACCOUNT", true);
            PaymentMethod cod = createPaymentMethod(PaymentType.COD, PaymentProvider.COD, "N/A", true);

            log.info("Seeded {} payment methods", paymentMethodRepository.count());

            // Seed Sample Product Generals - Fresh Food (Vietnamese)

            // Trái Cây
            ProductGeneral redApples = createProductGeneral(
                "Táo Đỏ Fuji",
                "Táo đỏ Fuji giòn ngọt, hoàn hảo cho bữa ăn nhẹ",
                new String[]{"tươi", "hữu-cơ", "theo-mùa", "vitamin-c"},
                Unit.KILOGRAM,
                1L,
                applesPears.getSubSubcategoryId()
            );

            ProductGeneral bananas = createProductGeneral(
                "Chuối Hữu Cơ",
                "Chuối hữu cơ chín mọng, giàu kali",
                new String[]{"tươi", "hữu-cơ", "nhiệt-đới", "năng-lượng"},
                Unit.KILOGRAM,
                1L,
                tropicalFruits.getSubSubcategoryId()
            );

            ProductGeneral oranges = createProductGeneral(
                "Cam Valencia",
                "Cam Valencia mọng nước, thích hợp vắt nước",
                new String[]{"tươi", "có-múi", "vitamin-c", "mọng-nước"},
                Unit.KILOGRAM,
                1L,
                citrusFruits.getSubSubcategoryId()
            );

            ProductGeneral strawberries = createProductGeneral(
                "Dâu Tây Tươi",
                "Dâu tây tươi ngọt thơm",
                new String[]{"tươi", "quả-mọng", "theo-mùa", "vitamin-c"},
                Unit.GRAM,
                250L,
                berries.getSubSubcategoryId()
            );

            // Rau Củ
            ProductGeneral spinach = createProductGeneral(
                "Rau Bina Non",
                "Lá rau bina non mềm, đã rửa sạch",
                new String[]{"tươi", "rau-lá-xanh", "sắt", "hữu-cơ"},
                Unit.GRAM,
                200L,
                leafyGreens.getSubSubcategoryId()
            );

            ProductGeneral carrots = createProductGeneral(
                "Cà Rốt Tươi",
                "Cà rốt cam giòn, giàu beta-carotene",
                new String[]{"tươi", "củ-quả", "vitamin-a", "hữu-cơ"},
                Unit.KILOGRAM,
                1L,
                rootVegetables.getSubSubcategoryId()
            );

            ProductGeneral tomatoes = createProductGeneral(
                "Cà Chua Cherry",
                "Cà chua cherry ngọt trên cây",
                new String[]{"tươi", "chín-cây", "salad", "hữu-cơ"},
                Unit.GRAM,
                250L,
                tomatoesCucumbers.getSubSubcategoryId()
            );

            ProductGeneral whiteMushrooms = createProductGeneral(
                "Nấm Trắng",
                "Nấm trắng tươi",
                new String[]{"tươi", "umami", "nấu-ăn", "protein"},
                Unit.GRAM,
                200L,
                mushrooms.getSubSubcategoryId()
            );

            // Thịt & Gia Cầm
            ProductGeneral chickenBreast = createProductGeneral(
                "Ức Gà Phi Lê",
                "Ức gà tươi không xương, không da",
                new String[]{"tươi", "protein", "ít-béo", "halal"},
                Unit.KILOGRAM,
                1L,
                chicken.getSubSubcategoryId()
            );

            ProductGeneral beefSteak = createProductGeneral(
                "Bít Tết Bò Ribeye",
                "Bít tết bò ribeye cao cấp có vân mỡ",
                new String[]{"tươi", "cao-cấp", "protein", "vân-mỡ"},
                Unit.GRAM,
                500L,
                beef.getSubSubcategoryId()
            );

            // Hải Sản
            ProductGeneral salmon = createProductGeneral(
                "Phi Lê Cá Hồi Đại Tây Dương",
                "Phi lê cá hồi Đại Tây Dương tươi, giàu Omega-3",
                new String[]{"tươi", "hải-sản", "omega-3", "cao-cấp"},
                Unit.GRAM,
                500L,
                fish.getSubSubcategoryId()
            );

            ProductGeneral shrimp = createProductGeneral(
                "Tôm Càng Tươi",
                "Tôm càng lớn, đã lột vỏ và bỏ chỉ",
                new String[]{"tươi", "hải-sản", "protein", "cao-cấp"},
                Unit.GRAM,
                300L,
                shellfish.getSubSubcategoryId()
            );

            // Sữa & Trứng
            ProductGeneral wholeMilk = createProductGeneral(
                "Sữa Tươi Nguyên Kem",
                "Sữa tươi nguyên kem 3.5% béo, nguồn gốc địa phương",
                new String[]{"sữa", "tươi", "canxi", "vitamin-d"},
                Unit.LITER,
                1L,
                freshMilk.getSubSubcategoryId()
            );

            ProductGeneral greekYogurt = createProductGeneral(
                "Sữa Chua Hy Lạp Nguyên Chất",
                "Sữa chua Hy Lạp đặc và béo",
                new String[]{"sữa", "probiotic", "protein", "lành-mạnh"},
                Unit.GRAM,
                500L,
                yogurt.getSubSubcategoryId()
            );

            ProductGeneral mozzarella = createProductGeneral(
                "Phô Mai Mozzarella Tươi",
                "Phô mai Mozzarella tươi dạng viên",
                new String[]{"sữa", "phô-mai", "ý", "mềm"},
                Unit.GRAM,
                250L,
                softCheese.getSubSubcategoryId()
            );

            ProductGeneral cheddar = createProductGeneral(
                "Phô Mai Cheddar Ủ",
                "Phô mai Cheddar ủ vị đậm dạng khối",
                new String[]{"sữa", "phô-mai", "ủ", "đậm-đà"},
                Unit.GRAM,
                400L,
                hardCheese.getSubSubcategoryId()
            );

            ProductGeneral eggs = createProductGeneral(
                "Trứng Gà Trang Trại",
                "Trứng gà thả vườn, size lớn",
                new String[]{"tươi", "protein", "thả-vườn", "omega-3"},
                Unit.DOZEN,
                12L,
                chickenEggs.getSubSubcategoryId()
            );

            // Bánh Mì & Bánh Ngọt
            ProductGeneral wholeWheatBread = createProductGeneral(
                "Bánh Mì Lúa Mì Nguyên Cám",
                "Bánh mì lúa mì nguyên cám nướng tươi",
                new String[]{"bánh", "tươi", "nguyên-cám", "chất-xơ"},
                Unit.PIECE,
                1L,
                wheatBread.getSubSubcategoryId()
            );

            ProductGeneral croissant = createProductGeneral(
                "Bánh Sừng Bò Bơ",
                "Bánh sừng bò Pháp giòn tan bơ thơm",
                new String[]{"bánh", "tươi", "pháp", "bơ"},
                Unit.PIECE,
                1L,
                croissants.getSubSubcategoryId()
            );

            // Đồ Uống
            ProductGeneral freshOrangeJuice = createProductGeneral(
                "Nước Cam Vắt Tươi",
                "Nước cam vắt tươi 100%, không chất phụ gia",
                new String[]{"đồ-uống", "tươi", "vitamin-c", "không-đường"},
                Unit.LITER,
                1L,
                orangeJuice.getSubSubcategoryId()
            );

            ProductGeneral almondMilkProduct = createProductGeneral(
                "Sữa Hạnh Nhân Không Đường",
                "Sữa hạnh nhân tươi không đường, không lactose",
                new String[]{"đồ-uống", "thực-vật", "không-lactose", "chay"},
                Unit.LITER,
                1L,
                almondMilk.getSubSubcategoryId()
            );

            log.info("Seeded {} sample fresh food products", productGeneralRepository.count());

            log.info("Database seeding completed successfully!");
        };
    }
    
    private User createUser(Long userId, String email, String fName, String lName, String avtUrl, LocalDate dob, String pNum, Gender gender, AccountStatus accStatus) {
        
        User user = new User();
        
        user.setUserId(userId);
        user.setEmail(email);
        user.setFName(fName);
        user.setLName(lName);
        user.setAvtUrl(avtUrl);
        user.setDob(dob);
        user.setPNum(pNum);
        user.setGender(gender);
        user.setAccStatus(accStatus);
        
        return userRepository.save(user);
    }
    
    private Buyer createBuyer(
            Long userId
    ) {
        Buyer buyer = new Buyer(userId);
        
        return buyerRepository.save(buyer);
    }

    private Category createCategory(String name, String description, Integer displayOrder,
                                   String iconUrl, String isSubCategory, Long belongToCategory) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setDisplayOrder(displayOrder);
        category.setIconUrl(iconUrl);
        category.setIsSubCategory(isSubCategory);
        category.setBelongToCategory(belongToCategory);
        return categoryRepository.save(category);
    }

    private SubSubcategory createSubSubcategory(String name, String description,
                                               String iconUrl, Long subcategoryId) {
        SubSubcategory subSubcategory = new SubSubcategory();
        subSubcategory.setName(name);
        subSubcategory.setDescription(description);
        subSubcategory.setIconUrl(iconUrl);
        subSubcategory.setSubcategoryId(subcategoryId);
        return subSubcategoryRepository.save(subSubcategory);
    }

    private PaymentMethod createPaymentMethod(PaymentType paymentType, PaymentProvider paymentProvider,
                                             String accountNum, Boolean isActive) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentType(paymentType);
        paymentMethod.setPaymentProvider(paymentProvider);
        paymentMethod.setAccountNum(accountNum);
        paymentMethod.setIsActive(isActive);
        return paymentMethodRepository.save(paymentMethod);
    }

    private ProductGeneral createProductGeneral(String name, String description, String[] tags,
                                               Unit unit, Long unitQuantity, Long subSubcategoryId) {
        ProductGeneral product = new ProductGeneral();
        product.setProdName(name);
        product.setDescription(description);
        product.setTags(tags);
        product.setUnit(unit);
        product.setUnitQuantity(unitQuantity);
        product.setSubSubcategoryId(subSubcategoryId);
        // Use default image URL from application.yaml
        product.setImgUrl("https://pub-0365edd1781141cdb68675969c7cdb87.r2.dev/5fe95e6c-c064-49c4-8712-1c8f54472502.jpg");
        return productGeneralRepository.save(product);
    }
}

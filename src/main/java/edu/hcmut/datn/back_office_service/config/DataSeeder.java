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
            
            // ==================== CATEGORY 1: Thịt & Hải Sản ====================
            Category thitHaiSan = createCategory("Thịt & Hải Sản", "Thịt tươi, gia cầm và hải sản", 1, null, "N", null);
            
            Category giaCam = createCategory("Gia Cầm", "Các loại thịt gia cầm tươi", 1, null, "Y", thitHaiSan.getCategoryId());
            createSubSubcategory("Thịt Gà", "Thịt gà tươi nguyên con và các phần", null, giaCam.getCategoryId());
            createSubSubcategory("Thịt Vịt", "Thịt vịt tươi nguyên con và các phần", null, giaCam.getCategoryId());
            createSubSubcategory("Thịt Ngan", "Thịt ngan tươi", null, giaCam.getCategoryId());
            createSubSubcategory("Thịt Chim Cút", "Chim cút tươi nguyên con", null, giaCam.getCategoryId());
            createSubSubcategory("Lòng Gia Cầm", "Gan, mề, tim gia cầm tươi", null, giaCam.getCategoryId());
            
            Category thitDo = createCategory("Thịt Đỏ", "Các loại thịt đỏ tươi", 2, null, "Y", thitHaiSan.getCategoryId());
            createSubSubcategory("Thịt Bò", "Thịt bò tươi các loại", null, thitDo.getCategoryId());
            createSubSubcategory("Thịt Heo", "Thịt heo tươi các loại", null, thitDo.getCategoryId());
            createSubSubcategory("Thịt Dê", "Thịt dê tươi", null, thitDo.getCategoryId());
            createSubSubcategory("Thịt Cừu", "Thịt cừu tươi nhập khẩu", null, thitDo.getCategoryId());
            createSubSubcategory("Xúc Xích Tươi", "Xúc xích tươi chưa qua chế biến", null, thitDo.getCategoryId());
            
            Category haiSan = createCategory("Hải Sản", "Hải sản tươi sống", 3, null, "Y", thitHaiSan.getCategoryId());
            createSubSubcategory("Tôm Tươi", "Tôm sú, tôm thẻ tươi sống", null, haiSan.getCategoryId());
            createSubSubcategory("Cá Tươi", "Các loại cá tươi nguyên con và phi lê", null, haiSan.getCategoryId());
            createSubSubcategory("Mực Tươi", "Mực ống, mực nang tươi", null, haiSan.getCategoryId());
            createSubSubcategory("Cua Ghẹ", "Cua biển, ghẹ tươi sống", null, haiSan.getCategoryId());
            createSubSubcategory("Nghêu Sò", "Nghêu, sò, hàu tươi", null, haiSan.getCategoryId());
            
            // ==================== CATEGORY 2: Rau Củ Quả ====================
            Category rauCuQua = createCategory("Rau Củ Quả", "Rau xanh, củ quả và trái cây tươi", 2, null, "N", null);
            
            Category rauAnLa = createCategory("Rau Ăn Lá", "Các loại rau xanh ăn lá", 1, null, "Y", rauCuQua.getCategoryId());
            createSubSubcategory("Rau Muống", "Rau muống tươi", null, rauAnLa.getCategoryId());
            createSubSubcategory("Cải Xanh", "Cải xanh, cải ngọt tươi", null, rauAnLa.getCategoryId());
            createSubSubcategory("Xà Lách", "Xà lách các loại tươi", null, rauAnLa.getCategoryId());
            createSubSubcategory("Rau Dền", "Rau dền đỏ và rau dền xanh", null, rauAnLa.getCategoryId());
            createSubSubcategory("Cải Thìa", "Cải thìa, cải bẹ trắng tươi", null, rauAnLa.getCategoryId());
            
            Category cuQua = createCategory("Củ Quả", "Các loại củ và quả tươi", 2, null, "Y", rauCuQua.getCategoryId());
            createSubSubcategory("Cà Rốt", "Cà rốt tươi", null, cuQua.getCategoryId());
            createSubSubcategory("Khoai Tây", "Khoai tây tươi", null, cuQua.getCategoryId());
            createSubSubcategory("Củ Cải", "Củ cải trắng, củ cải đỏ tươi", null, cuQua.getCategoryId());
            createSubSubcategory("Bắp", "Bắp ngô tươi các loại", null, cuQua.getCategoryId());
            createSubSubcategory("Su Su", "Su su tươi", null, cuQua.getCategoryId());
            
            Category traiCay = createCategory("Trái Cây", "Trái cây tươi trong nước và nhập khẩu", 3, null, "Y", rauCuQua.getCategoryId());
            createSubSubcategory("Xoài", "Xoài tươi các loại", null, traiCay.getCategoryId());
            createSubSubcategory("Chuối", "Chuối tươi các loại", null, traiCay.getCategoryId());
            createSubSubcategory("Dưa Hấu", "Dưa hấu tươi", null, traiCay.getCategoryId());
            createSubSubcategory("Ổi", "Ổi tươi các loại", null, traiCay.getCategoryId());
            createSubSubcategory("Thanh Long", "Thanh long ruột đỏ và ruột trắng", null, traiCay.getCategoryId());
            
            Category rauGiaVi = createCategory("Rau Gia Vị", "Các loại rau và củ gia vị", 4, null, "Y", rauCuQua.getCategoryId());
            createSubSubcategory("Hành Lá", "Hành lá tươi", null, rauGiaVi.getCategoryId());
            createSubSubcategory("Tỏi", "Tỏi tươi và tỏi khô", null, rauGiaVi.getCategoryId());
            createSubSubcategory("Gừng", "Gừng tươi", null, rauGiaVi.getCategoryId());
            createSubSubcategory("Ớt", "Ớt sừng, ớt hiểm tươi", null, rauGiaVi.getCategoryId());
            createSubSubcategory("Sả", "Sả tươi nguyên cây", null, rauGiaVi.getCategoryId());
            
            // ==================== CATEGORY 3: Sữa & Trứng ====================
            Category suaTrung = createCategory("Sữa & Trứng", "Sữa tươi, sản phẩm từ sữa và trứng", 3, null, "N", null);
            
            Category suaTuoi = createCategory("Sữa Tươi", "Các loại sữa tươi nguyên chất", 1, null, "Y", suaTrung.getCategoryId());
            createSubSubcategory("Sữa Tươi Không Đường", "Sữa tươi tiệt trùng không đường", null, suaTuoi.getCategoryId());
            createSubSubcategory("Sữa Tươi Có Đường", "Sữa tươi tiệt trùng có đường", null, suaTuoi.getCategoryId());
            createSubSubcategory("Sữa Hữu Cơ", "Sữa tươi hữu cơ nguyên chất", null, suaTuoi.getCategoryId());
            createSubSubcategory("Sữa Ít Béo", "Sữa tươi ít béo tách một phần kem", null, suaTuoi.getCategoryId());
            createSubSubcategory("Sữa Tách Béo", "Sữa tươi tách hoàn toàn chất béo", null, suaTuoi.getCategoryId());
            
            Category spTuSua = createCategory("Sản Phẩm Từ Sữa", "Bơ, phô mai, kem và sữa chua", 2, null, "Y", suaTrung.getCategoryId());
            createSubSubcategory("Bơ Tươi", "Bơ động vật tươi các loại", null, spTuSua.getCategoryId());
            createSubSubcategory("Phô Mai", "Phô mai tươi và phô mai chế biến", null, spTuSua.getCategoryId());
            createSubSubcategory("Kem Tươi", "Kem tươi whipping cream", null, spTuSua.getCategoryId());
            createSubSubcategory("Sữa Chua", "Sữa chua ăn các loại", null, spTuSua.getCategoryId());
            createSubSubcategory("Sữa Đặc", "Sữa đặc có đường và không đường", null, spTuSua.getCategoryId());
            
            Category trung = createCategory("Trứng", "Các loại trứng tươi", 3, null, "Y", suaTrung.getCategoryId());
            createSubSubcategory("Trứng Gà Công Nghiệp", "Trứng gà tươi công nghiệp", null, trung.getCategoryId());
            createSubSubcategory("Trứng Gà Ta", "Trứng gà ta thả vườn", null, trung.getCategoryId());
            createSubSubcategory("Trứng Vịt", "Trứng vịt tươi", null, trung.getCategoryId());
            createSubSubcategory("Trứng Cút", "Trứng cút tươi", null, trung.getCategoryId());
            createSubSubcategory("Trứng Vịt Lộn", "Trứng vịt lộn ấp sẵn", null, trung.getCategoryId());
            
            Category doUongTuSua = createCategory("Đồ Uống Từ Sữa", "Sữa đậu nành, sữa hạt và các loại sữa uống", 4, null, "Y", suaTrung.getCategoryId());
            createSubSubcategory("Sữa Đậu Nành", "Sữa đậu nành tươi nguyên chất", null, doUongTuSua.getCategoryId());
            createSubSubcategory("Sữa Hạt", "Sữa hạnh nhân, sữa óc chó, sữa hạt điều", null, doUongTuSua.getCategoryId());
            createSubSubcategory("Yaourt Uống", "Sữa chua uống các loại", null, doUongTuSua.getCategoryId());
            createSubSubcategory("Kefir", "Kefir lên men tự nhiên", null, doUongTuSua.getCategoryId());
            createSubSubcategory("Sữa Chua Uống Nha Đam", "Sữa chua uống kết hợp nha đam", null, doUongTuSua.getCategoryId());

            log.info("Seeded {} sub-subcategories", subSubcategoryRepository.count());

            // Seed Payment Methods
            PaymentMethod momo = createPaymentMethod(PaymentType.E_WALLET, PaymentProvider.MOMO, "MOMO_ACCOUNT", true);
            PaymentMethod zalopay = createPaymentMethod(PaymentType.E_WALLET, PaymentProvider.ZALOPAY, "ZALOPAY_ACCOUNT", true);
            PaymentMethod cod = createPaymentMethod(PaymentType.COD, PaymentProvider.COD, "N/A", true);

            log.info("Seeded {} payment methods", paymentMethodRepository.count());

            // Seed Sample Product Generals - Fresh Food (Vietnamese)
                    // Thịt Gà (subSubcategoryId: 1)
            createProductGeneral("Gà Ta Nguyên Con", "Gà ta thả vườn tươi ngon, thịt chắc thơm ngon", new String[]{"gà ta", "gà nguyên con", "thịt gà"}, Unit.KILOGRAM, 1L, 1L);
            createProductGeneral("Ức Gà Phi Lê", "Ức gà phi lê không xương, thịt trắng mềm", new String[]{"ức gà", "phi lê", "thịt gà"}, Unit.GRAM, 500L, 1L);
            createProductGeneral("Đùi Gà Tươi", "Đùi gà tươi có xương, thịt ngọt đậm đà", new String[]{"đùi gà", "thịt gà", "gà tươi"}, Unit.GRAM, 500L, 1L);

            // Thịt Vịt (subSubcategoryId: 2)
            createProductGeneral("Vịt Trời Nguyên Con", "Vịt trời tươi nguyên con, thịt chắc thơm", new String[]{"vịt trời", "vịt nguyên con", "thịt vịt"}, Unit.KILOGRAM, 1L, 2L);
            createProductGeneral("Ức Vịt Phi Lê", "Ức vịt phi lê không da, ít mỡ", new String[]{"ức vịt", "phi lê", "thịt vịt"}, Unit.GRAM, 500L, 2L);
            createProductGeneral("Đùi Vịt Bó Xôi", "Đùi vịt bó xôi tươi ngon, thịt đậm đà", new String[]{"đùi vịt", "vịt bó xôi", "thịt vịt"}, Unit.GRAM, 500L, 2L);

            // Thịt Ngan (subSubcategoryId: 3)
            createProductGeneral("Ngan Nguyên Con", "Ngan tươi nguyên con, thịt thơm béo", new String[]{"ngan", "ngan nguyên con", "thịt ngan"}, Unit.KILOGRAM, 1L, 3L);
            createProductGeneral("Ức Ngan Phi Lê", "Ức ngan phi lê cao cấp, thịt đỏ thơm", new String[]{"ức ngan", "phi lê", "thịt ngan"}, Unit.GRAM, 500L, 3L);
            createProductGeneral("Đùi Ngan Tươi", "Đùi ngan tươi ngon, thịt chắc", new String[]{"đùi ngan", "thịt ngan", "ngan tươi"}, Unit.GRAM, 500L, 3L);

            // Thịt Chim Cút (subSubcategoryId: 4)
            createProductGeneral("Chim Cút Nguyên Con", "Chim cút tươi nguyên con, thịt thơm ngọt", new String[]{"chim cút", "cút nguyên con", "thịt cút"}, Unit.GRAM, 200L, 4L);
            createProductGeneral("Chim Cút Rút Xương", "Chim cút rút xương sẵn, tiện chế biến", new String[]{"chim cút", "rút xương", "thịt cút"}, Unit.GRAM, 200L, 4L);
            createProductGeneral("Chim Cút Loại 1", "Chim cút loại 1 to đều, tươi ngon", new String[]{"chim cút", "cút loại 1", "thịt cút"}, Unit.GRAM, 300L, 4L);

            // Lòng Gia Cầm (subSubcategoryId: 5)
            createProductGeneral("Gan Gà Tươi", "Gan gà tươi ngon, giàu dinh dưỡng", new String[]{"gan gà", "lòng gà", "nội t장"}, Unit.GRAM, 300L, 5L);
            createProductGeneral("Mề Gà Tươi", "Mề gà tươi sạch, giòn ngọt", new String[]{"mề gà", "lòng gà", "dạ dày gà"}, Unit.GRAM, 300L, 5L);
            createProductGeneral("Tim Gà Tươi", "Tim gà tươi ngon, dai giòn", new String[]{"tim gà", "lòng gà", "nội tạng"}, Unit.GRAM, 200L, 5L);

            // Thịt Bò (subSubcategoryId: 6)
            createProductGeneral("Thịt Bò Úc Phi Lê", "Thịt bò Úc nhập khẩu phi lê mềm", new String[]{"thịt bò", "bò Úc", "phi lê"}, Unit.GRAM, 500L, 6L);
            createProductGeneral("Thịt Bò Nạm", "Thịt bò nạm tươi, thích hợp nấu phở", new String[]{"thịt bò", "bò nạm", "bò tươi"}, Unit.GRAM, 500L, 6L);
            createProductGeneral("Thịt Bò Vai", "Thịt bò vai tươi ngon, thơm mềm", new String[]{"thịt bò", "bò vai", "bò tươi"}, Unit.GRAM, 500L, 6L);

            // Thịt Heo (subSubcategoryId: 7)
            createProductGeneral("Thịt Heo Ba Chỉ", "Thịt heo ba chỉ tươi, vừa nạc vừa mỡ", new String[]{"thịt heo", "ba chỉ", "thịt lợn"}, Unit.GRAM, 500L, 7L);
            createProductGeneral("Thịt Heo Nạc Vai", "Thịt heo nạc vai tươi, ít mỡ", new String[]{"thịt heo", "nạc vai", "thịt lợn"}, Unit.GRAM, 500L, 7L);
            createProductGeneral("Thịt Heo Nạc Dăm", "Thịt heo nạc dăm tươi ngon", new String[]{"thịt heo", "nạc dăm", "thịt lợn"}, Unit.GRAM, 500L, 7L);

            // Thịt Dê (subSubcategoryId: 8)
            createProductGeneral("Thịt Dê Nạc", "Thịt dê nạc tươi, thơm ngon bổ dưỡng", new String[]{"thịt dê", "dê nạc", "dê tươi"}, Unit.GRAM, 500L, 8L);
            createProductGeneral("Thịt Dê Có Xương", "Thịt dê có xương nấu cháo, hầm", new String[]{"thịt dê", "dê xương", "dê tươi"}, Unit.GRAM, 500L, 8L);
            createProductGeneral("Sườn Dê Tươi", "Sườn dê tươi ngon, nướng hoặc hầm", new String[]{"sườn dê", "thịt dê", "dê tươi"}, Unit.GRAM, 500L, 8L);

            // Thịt Cừu (subSubcategoryId: 9)
            createProductGeneral("Thịt Cừu Úc Phi Lê", "Thịt cừu Úc nhập khẩu phi lê cao cấp", new String[]{"thịt cừu", "cừu Úc", "phi lê"}, Unit.GRAM, 500L, 9L);
            createProductGeneral("Thịt Cừu New Zealand", "Thịt cừu New Zealand tươi ngon", new String[]{"thịt cừu", "cừu NZ", "cừu nhập khẩu"}, Unit.GRAM, 500L, 9L);
            createProductGeneral("Sườn Cừu Cao Cấp", "Sườn cừu cao cấp nướng BBQ", new String[]{"sườn cừu", "thịt cừu", "cừu nướng"}, Unit.GRAM, 500L, 9L);

            // Xúc Xích Tươi (subSubcategoryId: 10)
            createProductGeneral("Xúc Xích Heo Tươi", "Xúc xích heo tươi chưa nướng", new String[]{"xúc xích", "xúc xích heo", "lạp xưởng"}, Unit.GRAM, 500L, 10L);
            createProductGeneral("Xúc Xích Bò Tươi", "Xúc xích bò tươi nguyên chất", new String[]{"xúc xích", "xúc xích bò", "lạp xưởng"}, Unit.GRAM, 500L, 10L);
            createProductGeneral("Xúc Xích Gà Tươi", "Xúc xích gà tươi ít béo, healthy", new String[]{"xúc xích", "xúc xích gà", "lạp xưởng"}, Unit.GRAM, 500L, 10L);

            // Tôm Tươi (subSubcategoryId: 11)
            createProductGeneral("Tôm Sú Tươi Sống", "Tôm sú tươi sống size lớn", new String[]{"tôm sú", "tôm tươi", "hải sản"}, Unit.KILOGRAM, 1L, 11L);
            createProductGeneral("Tôm Thẻ Tươi", "Tôm thẻ tươi ngọt thịt chắc", new String[]{"tôm thẻ", "tôm tươi", "hải sản"}, Unit.GRAM, 500L, 11L);
            createProductGeneral("Tôm Càng Xanh", "Tôm càng xanh tươi sống to", new String[]{"tôm càng", "tôm tươi", "hải sản"}, Unit.KILOGRAM, 1L, 11L);

            // Cá Tươi (subSubcategoryId: 12)
            createProductGeneral("Cá Hồi Phi Lê", "Cá hồi phi lê tươi Nauy", new String[]{"cá hồi", "phi lê", "cá tươi"}, Unit.GRAM, 500L, 12L);
            createProductGeneral("Cá Basa Phi Lê", "Cá basa phi lê không xương", new String[]{"cá basa", "phi lê", "cá tươi"}, Unit.GRAM, 500L, 12L);
            createProductGeneral("Cá Diêu Hồng Tươi", "Cá diêu hồng tươi nguyên con", new String[]{"cá diêu hồng", "cá nguyên con", "cá tươi"}, Unit.KILOGRAM, 1L, 12L);

            // Mực Tươi (subSubcategoryId: 13)
            createProductGeneral("Mực Ống Tươi", "Mực ống tươi size lớn", new String[]{"mực ống", "mực tươi", "hải sản"}, Unit.KILOGRAM, 1L, 13L);
            createProductGeneral("Mực Nang Tươi", "Mực nang tươi ngọt thịt dai", new String[]{"mực nang", "mực tươi", "hải sản"}, Unit.GRAM, 500L, 13L);
            createProductGeneral("Mực Lá Tươi", "Mực lá tươi sống nhỏ", new String[]{"mực lá", "mực tươi", "hải sản"}, Unit.GRAM, 500L, 13L);

            // Cua Ghẹ (subSubcategoryId: 14)
            createProductGeneral("Cua Biển Tươi Sống", "Cua biển tươi sống size to", new String[]{"cua biển", "cua tươi", "hải sản"}, Unit.KILOGRAM, 1L, 14L);
            createProductGeneral("Ghẹ Xanh Tươi", "Ghẹ xanh tươi sống thịt ngọt", new String[]{"ghẹ", "cua tươi", "hải sản"}, Unit.KILOGRAM, 1L, 14L);
            createProductGeneral("Cua Gạch Tươi", "Cua gạch tươi đầy gạch béo ngậy", new String[]{"cua gạch", "cua tươi", "hải sản"}, Unit.KILOGRAM, 1L, 14L);

            // Nghêu Sò (subSubcategoryId: 15)
            createProductGeneral("Nghêu Tươi Sống", "Nghêu tươi sống vỏ to thịt ngọt", new String[]{"nghêu", "sò tươi", "hải sản"}, Unit.KILOGRAM, 1L, 15L);
            createProductGeneral("Sò Huyết Tươi", "Sò huyết tươi sống ngon ngọt", new String[]{"sò huyết", "sò tươi", "hải sản"}, Unit.KILOGRAM, 1L, 15L);
            createProductGeneral("Hàu Sữa Tươi", "Hàu sữa tươi sống béo ngậy", new String[]{"hàu", "sò tươi", "hải sản"}, Unit.KILOGRAM, 1L, 15L);

            // Rau Muống (subSubcategoryId: 16)
            createProductGeneral("Rau Muống Xanh", "Rau muống xanh tươi non mơn mởn", new String[]{"rau muống", "rau xanh", "rau ăn lá"}, Unit.GRAM, 500L, 16L);
            createProductGeneral("Rau Muống Dại", "Rau muống dại tươi thơm ngon", new String[]{"rau muống", "rau dại", "rau xanh"}, Unit.GRAM, 500L, 16L);
            createProductGeneral("Rau Muống Cọng To", "Rau muống cọng to tươi giòn", new String[]{"rau muống", "rau xanh", "rau ăn lá"}, Unit.GRAM, 500L, 16L);

            // Cải Xanh (subSubcategoryId: 17)
            createProductGeneral("Cải Xanh Tươi", "Cải xanh tươi ngọt mát", new String[]{"cải xanh", "rau xanh", "rau ăn lá"}, Unit.GRAM, 500L, 17L);
            createProductGeneral("Cải Ngọt Baby", "Cải ngọt baby non mềm", new String[]{"cải ngọt", "rau xanh", "rau ăn lá"}, Unit.GRAM, 300L, 17L);
            createProductGeneral("Cải Ngồng Tươi", "Cải ngồng tươi giòn ngọt", new String[]{"cải ngồng", "rau xanh", "rau ăn lá"}, Unit.GRAM, 500L, 17L);

            // Xà Lách (subSubcategoryId: 18)
            createProductGeneral("Xà Lách Xoong", "Xà lách xoong tươi giòn", new String[]{"xà lách", "rau xanh", "rau salad"}, Unit.GRAM, 300L, 18L);
            createProductGeneral("Xà Lách Lô Lô", "Xà lách lô lô đỏ tím tươi", new String[]{"xà lách", "rau xanh", "rau salad"}, Unit.GRAM, 300L, 18L);
            createProductGeneral("Xà Lách Tím", "Xà lách tím tươi giàu anthocyanin", new String[]{"xà lách", "rau xanh", "rau salad"}, Unit.GRAM, 300L, 18L);

            // Rau Dền (subSubcategoryId: 19)
            createProductGeneral("Rau Dền Đỏ", "Rau dền đỏ tươi giàu sắt", new String[]{"rau dền", "rau đỏ", "rau xanh"}, Unit.GRAM, 500L, 19L);
            createProductGeneral("Rau Dền Xanh", "Rau dền xanh tươi mát", new String[]{"rau dền", "rau xanh", "rau ăn lá"}, Unit.GRAM, 500L, 19L);
            createProductGeneral("Rau Dền Cơm", "Rau dền cơm non mềm thơm", new String[]{"rau dền", "rau xanh", "rau ăn lá"}, Unit.GRAM, 500L, 19L);

            // Cải Thìa (subSubcategoryId: 20)
            createProductGeneral("Cải Thìa Tươi", "Cải thìa tươi ngọt mát", new String[]{"cải thìa", "rau xanh", "rau ăn lá"}, Unit.GRAM, 500L, 20L);
            createProductGeneral("Cải Bẹ Trắng", "Cải bẹ trắng tươi giòn ngọt", new String[]{"cải bẹ", "rau xanh", "rau ăn lá"}, Unit.GRAM, 500L, 20L);
            createProductGeneral("Cải Thìa Baby", "Cải thìa baby non mềm", new String[]{"cải thìa", "rau xanh", "rau ăn lá"}, Unit.GRAM, 300L, 20L);

            // Cà Rốt (subSubcategoryId: 21)
            createProductGeneral("Cà Rốt Đà Lạt", "Cà rốt Đà Lạt tươi giòn ngọt", new String[]{"cà rốt", "củ quả", "rau củ"}, Unit.GRAM, 500L, 21L);
            createProductGeneral("Cà Rốt Nhật", "Cà rốt Nhật to đều màu đẹp", new String[]{"cà rốt", "củ quả", "rau củ"}, Unit.KILOGRAM, 1L, 21L);
            createProductGeneral("Cà Rốt Baby", "Cà rốt baby nhỏ xinh ăn salad", new String[]{"cà rốt", "củ quả", "rau củ"}, Unit.GRAM, 300L, 21L);

            // Khoai Tây (subSubcategoryId: 22)
            createProductGeneral("Khoai Tây Đà Lạt", "Khoai tây Đà Lạt tươi ngon", new String[]{"khoai tây", "củ quả", "rau củ"}, Unit.KILOGRAM, 1L, 22L);
            createProductGeneral("Khoai Tây Ai Cập", "Khoai tây Ai Cập nhập khẩu", new String[]{"khoai tây", "củ quả", "rau củ"}, Unit.KILOGRAM, 1L, 22L);
            createProductGeneral("Khoai Tây Tím", "Khoai tây tím giàu chất chống oxy hóa", new String[]{"khoai tây", "củ quả", "rau củ"}, Unit.GRAM, 500L, 22L);

            // Củ Cải (subSubcategoryId: 23)
            createProductGeneral("Củ Cải Trắng", "Củ cải trắng tươi giòn ngọt", new String[]{"củ cải", "củ quả", "rau củ"}, Unit.KILOGRAM, 1L, 23L);
            createProductGeneral("Củ Cải Đỏ", "Củ cải đỏ tươi giàu vitamin", new String[]{"củ cải", "củ quả", "rau củ"}, Unit.GRAM, 500L, 23L);
            createProductGeneral("Củ Cải Muối", "Củ cải trắng dùng làm dưa muối", new String[]{"củ cải", "củ quả", "rau củ"}, Unit.KILOGRAM, 1L, 23L);

            // Bắp (subSubcategoryId: 24)
            createProductGeneral("Bắp Ngọt Tươi", "Bắp ngọt tươi hạt vàng căng mọng", new String[]{"bắp ngô", "ngô", "rau củ"}, Unit.KILOGRAM, 1L, 24L);
            createProductGeneral("Bắp Nếp Tươi", "Bắp nếp tươi dẻo thơm", new String[]{"bắp nếp", "ngô", "rau củ"}, Unit.KILOGRAM, 1L, 24L);
            createProductGeneral("Bắp Mỹ Tươi", "Bắp Mỹ tươi hạt to ngọt", new String[]{"bắp ngô", "ngô Mỹ", "rau củ"}, Unit.KILOGRAM, 1L, 24L);

            // Su Su (subSubcategoryId: 25)
            createProductGeneral("Su Su Xanh", "Su su xanh tươi giòn ngọt", new String[]{"su su", "củ quả", "rau củ"}, Unit.KILOGRAM, 1L, 25L);
            createProductGeneral("Su Su Trắng", "Su su trắng tươi mềm ngọt", new String[]{"su su", "củ quả", "rau củ"}, Unit.KILOGRAM, 1L, 25L);
            createProductGeneral("Su Su Non", "Su su non tươi giòn ăn salad", new String[]{"su su", "củ quả", "rau củ"}, Unit.GRAM, 500L, 25L);

            // Xoài (subSubcategoryId: 26)
            createProductGeneral("Xoài Cát Hòa Lộc", "Xoài cát Hòa Lộc ngọt thơm", new String[]{"xoài", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 26L);
            createProductGeneral("Xoài Úc", "Xoài Úc nhập khẩu to ngọt", new String[]{"xoài", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 26L);
            createProductGeneral("Xoài Tượng", "Xoài tượng xanh giòn chua ngọt", new String[]{"xoài", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 26L);

            // Chuối (subSubcategoryId: 27)
            createProductGeneral("Chuối Già", "Chuối già ngọt thơm bổ dưỡng", new String[]{"chuối", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 27L);
            createProductGeneral("Chuối Tiêu Hương", "Chuối tiêu hương thơm ngọt", new String[]{"chuối", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 27L);
            createProductGeneral("Chuối Sứ", "Chuối sứ nhỏ ngọt đậm", new String[]{"chuối", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 27L);

            // Dưa Hấu (subSubcategoryId: 28)
            createProductGeneral("Dưa Hấu Không Hạt", "Dưa hấu không hạt ngọt mát", new String[]{"dưa hấu", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 28L);
            createProductGeneral("Dưa Hấu Ruột Đỏ", "Dưa hấu ruột đỏ ngọt tươi", new String[]{"dưa hấu", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 28L);
            createProductGeneral("Dưa Hấu Vàng", "Dưa hấu vàng giòn ngọt thanh", new String[]{"dưa hấu", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 28L);

            // Ổi (subSubcategoryId: 29)
            createProductGeneral("Ổi Nữ Hoàng", "Ổi nữ hoàng giòn ngọt thơm", new String[]{"ổi", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 29L);
            createProductGeneral("Ổi Ruột Đỏ", "Ổi ruột đỏ ngọt giàu lycopene", new String[]{"ổi", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 29L);
            createProductGeneral("Ổi Xanh", "Ổi xanh giòn chua nhẹ", new String[]{"ổi", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 29L);

            // Thanh Long (subSubcategoryId: 30)
            createProductGeneral("Thanh Long Ruột Đỏ", "Thanh long ruột đỏ ngọt thơm", new String[]{"thanh long", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 30L);
            createProductGeneral("Thanh Long Ruột Trắng", "Thanh long ruột trắng ngọt mát", new String[]{"thanh long", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 30L);
            createProductGeneral("Thanh Long Vàng", "Thanh long vàng Ecuador cao cấp", new String[]{"thanh long", "trái cây", "hoa quả"}, Unit.KILOGRAM, 1L, 30L);

            // Hành Lá (subSubcategoryId: 31)
            createProductGeneral("Hành Lá Tươi", "Hành lá tươi thơm dùng nêm nếm", new String[]{"hành lá", "gia vị", "rau thơm"}, Unit.GRAM, 200L, 31L);
            createProductGeneral("Hành Tây Tươi", "Hành tây tươi cay thơm", new String[]{"hành tây", "gia vị", "rau thơm"}, Unit.KILOGRAM, 1L, 31L);
            createProductGeneral("Hành Tím Tươi", "Hành tím tươi cay nồng", new String[]{"hành tím", "gia vị", "rau thơm"}, Unit.GRAM, 500L, 31L);

            // Tỏi (subSubcategoryId: 32)
            createProductGeneral("Tỏi Lý Sơn", "Tỏi Lý Sơn đặc sản thơm cay", new String[]{"tỏi", "gia vị", "rau thơm"}, Unit.GRAM, 500L, 32L);
            createProductGeneral("Tỏi Tươi Cà Mau", "Tỏi tươi Cà Mau múi to", new String[]{"tỏi", "gia vị", "rau thơm"}, Unit.GRAM, 500L, 32L);
            createProductGeneral("Tỏi Tây", "Tỏi tây nhập khẩu múi lớn", new String[]{"tỏi", "gia vị", "rau thơm"}, Unit.GRAM, 500L, 32L);

            // Gừng (subSubcategoryId: 33)
            createProductGeneral("Gừng Già", "Gừng già cay nồng dùng nấu ăn", new String[]{"gừng", "gia vị", "rau củ"}, Unit.GRAM, 500L, 33L);
            createProductGeneral("Gừng Non", "Gừng non ít cay giòn ngọt", new String[]{"gừng", "gia vị", "rau củ"}, Unit.GRAM, 300L, 33L);
            createProductGeneral("Gừng Khô", "Gừng khô cay thơm lâu", new String[]{"gừng", "gia vị", "rau củ"}, Unit.GRAM, 200L, 33L);

            // Ớt (subSubcategoryId: 34)
            createProductGeneral("Ớt Sừng Xanh", "Ớt sừng xanh tươi cay nhẹ", new String[]{"ớt", "gia vị", "rau thơm"}, Unit.GRAM, 200L, 34L);
            createProductGeneral("Ớt Hiểm Đỏ", "Ớt hiểm đỏ tươi cay nồng", new String[]{"ớt", "gia vị", "rau thơm"}, Unit.GRAM, 200L, 34L);
            createProductGeneral("Ớt Chuông", "Ớt chuông tươi ngọt màu sắc", new String[]{"ớt", "gia vị", "rau thơm"}, Unit.GRAM, 500L, 34L);

            // Sả (subSubcategoryId: 35)
            createProductGeneral("Sả Tươi Nguyên Cây", "Sả tươi nguyên cây thơm nồng", new String[]{"sả", "gia vị", "rau thơm"}, Unit.GRAM, 300L, 35L);
            createProductGeneral("Sả Tía Tươi", "Sả tía tươi thơm đặc biệt", new String[]{"sả", "gia vị", "rau thơm"}, Unit.GRAM, 300L, 35L);
            createProductGeneral("Sả Tôm Tươi", "Sả tôm tươi thơm nhẹ", new String[]{"sả", "gia vị", "rau thơm"}, Unit.GRAM, 300L, 35L);

            log.info("Seeded {} sample fresh food products", productGeneralRepository.count());
            
            // Seed User
            
            User user1 = createUser(
                    2L,
                    "buyer@gmail.com",
                    "Fbuyer",
                    "Lbuyer",
                    "https://pub-954e99f131cf4cc896de1ad360338682.r2.dev/128c271e-c0a6-433e-bcd1-f3bbc4243401-default-user-avt.png",
                    LocalDate.of(2004, 3, 20),
                    "0123456789",
                    Gender.MALE,
                    AccountStatus.ACTIVE
            );
            
            User user2 = createUser(
                    3L,
                    "minh.tran@gmail.com",
                    "minh",
                    "tran",
                    "https://pub-954e99f131cf4cc896de1ad360338682.r2.dev/128c271e-c0a6-433e-bcd1-f3bbc4243401-default-user-avt.png",
                    LocalDate.of(2004, 3, 20),
                    "0123456789",
                    Gender.MALE,
                    AccountStatus.ACTIVE
            );
            
            User user3 = createUser(
                    4L,
                    "huong.le@gmail.com",
                    "huong",
                    "le",
                    "https://pub-954e99f131cf4cc896de1ad360338682.r2.dev/128c271e-c0a6-433e-bcd1-f3bbc4243401-default-user-avt.png",
                    LocalDate.of(2004, 3, 20),
                    "0123456789",
                    Gender.FEMALE,
                    AccountStatus.ACTIVE
            );
            
            User user4 = createUser(
                    5L,
                    "tuan.pham@gmail.com",
                    "tuan",
                    "pham",
                    "https://pub-954e99f131cf4cc896de1ad360338682.r2.dev/128c271e-c0a6-433e-bcd1-f3bbc4243401-default-user-avt.png",
                    LocalDate.of(2004, 3, 20),
                    "0123456789",
                    Gender.MALE,
                    AccountStatus.ACTIVE
            );
            
            User user5 = createUser(
                    6L,
                    "linh.vo@gmail.com",
                    "linh",
                    "vo",
                    "https://pub-954e99f131cf4cc896de1ad360338682.r2.dev/128c271e-c0a6-433e-bcd1-f3bbc4243401-default-user-avt.png",
                    LocalDate.of(2004, 3, 20),
                    "0123456789",
                    Gender.FEMALE,
                    AccountStatus.ACTIVE
            );
            
            log.info("Seeded {} sample users", userRepository.count());
            
            // Seed Buyer
            
            Buyer buyer1 = createBuyer(user1.getUserId());
            Buyer buyer2 = createBuyer(user2.getUserId());
            Buyer buyer3 = createBuyer(user3.getUserId());
            Buyer buyer4 = createBuyer(user4.getUserId());
            Buyer buyer5 = createBuyer(user5.getUserId());
            
            log.info("Seeded {} sample buyers", userRepository.count());

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

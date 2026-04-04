package edu.hcmut.datn.back_office_service.service.impl;

import java.util.List;

import edu.hcmut.datn.back_office_service.dao.SubSubcategory;
import edu.hcmut.datn.back_office_service.messaging.productgeneral.ProductGeneralCreatedEvent;
import edu.hcmut.datn.back_office_service.messaging.productgeneral.ProductGeneralProducer;
import edu.hcmut.datn.back_office_service.repository.SubSubcategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.hcmut.datn.back_office_service.dao.ProductGeneral;
import edu.hcmut.datn.back_office_service.exception.productgeneral.ProductGeneralNotFoundException;
import edu.hcmut.datn.back_office_service.repository.ProductGeneralRepository;
import edu.hcmut.datn.back_office_service.service.ProductGeneralService;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductGeneralServiceImpl implements ProductGeneralService {

    private final ProductGeneralRepository productGeneralRepository;

    private final SubSubcategoryRepository subSubcategoryRepository;
    
    private final ProductGeneralProducer productGeneralProducer;
    
    
    
    @Override
    @Transactional
    public ProductGeneral create(ProductGeneral productGeneral) {
        // Save the product general
        ProductGeneral saved = productGeneralRepository.save(productGeneral);
        
        // Query to get the subcategory ID (parent of sub-subcategory)
        Long subcategoryId = null;
        if (saved.getSubSubcategoryId() != null) {
            SubSubcategory subSubcategory = subSubcategoryRepository
                    .findById(saved.getSubSubcategoryId())
                    .orElse(null);
            
            if (subSubcategory != null) {
                subcategoryId = subSubcategory.getSubcategoryId();
            }
        }
        
        // Publish event with both subSubcategoryId and derived categoryId
        ProductGeneralCreatedEvent event = new ProductGeneralCreatedEvent(
                saved.getProdGenId(),
                saved.getProdName(),
                null,  // imgUrl - to be added if needed
                null,  // description - to be added if needed
                saved.getSubSubcategoryId(),
                subcategoryId  // This is the subcategory ID for ecommerce
        );
        
        productGeneralProducer.publishProductGeneralCreated(event);
        log.info("Published ProductGeneralCreatedEvent for product: {}", saved.getProdGenId());
        
        return saved;
    }

    @Override
    public ProductGeneral read(Long productGeneralId) {
        return productGeneralRepository.findById(productGeneralId)
                .orElseThrow(() -> new ProductGeneralNotFoundException("Product General Not Found"));
    }

    @Override
    public List<ProductGeneral> readAll(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        Page<ProductGeneral> page = productGeneralRepository.findAll(pageable);

        return page.toList();
    }

    @Override
    public ProductGeneral update(Long productGeneralId, ProductGeneral productGeneral) {
        ProductGeneral cur = read(productGeneralId);
        
        if (productGeneral.getProdName() != null) {
            cur.setProdName(productGeneral.getProdName());
        }
        
        if (productGeneral.getImgUrl() != null) {
            cur.setImgUrl(productGeneral.getImgUrl());
        }
        
        if (productGeneral.getDescription() != null) {
            cur.setDescription(productGeneral.getDescription());
        }
        
        if (productGeneral.getSubSubcategoryId() != null) {
            cur.setSubSubcategoryId(productGeneral.getSubSubcategoryId());
        }
        
        if (productGeneral.getPreorderPolicyId() != null) {
            cur.setPreorderPolicyId(productGeneral.getPreorderPolicyId());
        }
        
        if (productGeneral.getEnterpriseStoreId() != null) {
            cur.setEnterpriseStoreId(productGeneral.getEnterpriseStoreId());
        }

        return productGeneralRepository.save(cur);
    }

    @Override
    public void delete(Long productGeneralId) {
        productGeneralRepository.delete(read(productGeneralId));
    }

    @Override
    public ProductGeneral updateProductMainImage(Long productGeneralId, String imageUrl) {
        ProductGeneral curProductGeneral = read(productGeneralId);

        return productGeneralRepository.save(curProductGeneral);
    }
}

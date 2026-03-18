package edu.hcmut.datn.back_office_service.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.hcmut.datn.back_office_service.dao.ProductGeneral;
import edu.hcmut.datn.back_office_service.exception.productgeneral.ProductGeneralNotFoundException;
import edu.hcmut.datn.back_office_service.repository.ProductGeneralRepository;
import edu.hcmut.datn.back_office_service.service.ProductGeneralService;

@Service
public class ProductGeneralServiceImpl implements ProductGeneralService {

    private final ProductGeneralRepository productGeneralRepository;

    public ProductGeneralServiceImpl(ProductGeneralRepository productGeneralRepository) {
        this.productGeneralRepository = productGeneralRepository;
    }

    @Override
    public ProductGeneral create(ProductGeneral productGeneral) {
        return productGeneralRepository.save(productGeneral);
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

        if (productGeneral.getPreorderPolicyId() != null) {
            cur.setPreorderPolicyId(productGeneral.getPreorderPolicyId());
        }

        cur.setEnterpriseStoreId(productGeneral.getEnterpriseStoreId());

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

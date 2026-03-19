package edu.hcmut.datn.back_office_service.service.impl;

import edu.hcmut.datn.back_office_service.dao.Category;
import edu.hcmut.datn.back_office_service.repository.CategoryRepository;
import edu.hcmut.datn.back_office_service.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }
}

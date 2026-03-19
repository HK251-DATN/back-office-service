package edu.hcmut.datn.back_office_service.messaging.category;

import edu.hcmut.datn.back_office_service.common.enums.AccountStatus;
import edu.hcmut.datn.back_office_service.common.enums.Gender;
import edu.hcmut.datn.back_office_service.dao.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
public class CategoryCreatedEvent {

    @Getter
    private Long categoryId;


    @Getter
    private String name;


    @Getter
    private String description;

    public Category toCategoryEntity() {
        Category newCategory = new Category();

        newCategory.setCategoryId(categoryId);
        newCategory.setName(name);
        newCategory.setDescription(description);

        return newCategory;
    }
}

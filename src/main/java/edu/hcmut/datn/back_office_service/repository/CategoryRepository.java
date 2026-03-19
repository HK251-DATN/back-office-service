package edu.hcmut.datn.back_office_service.repository;

import edu.hcmut.datn.back_office_service.dao.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

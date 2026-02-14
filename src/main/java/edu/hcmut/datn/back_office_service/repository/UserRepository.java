package edu.hcmut.datn.back_office_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hcmut.datn.back_office_service.dao.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}

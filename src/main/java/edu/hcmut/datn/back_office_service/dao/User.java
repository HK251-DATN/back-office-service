package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.AccountStatus;
import edu.hcmut.datn.back_office_service.common.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    @Column(name="user_id")
    @Id
    private Long userId;

    @Column(name="email")
    private String email;

    @Column(name="f_name")
    private String fName;
    
    @Column(name="l_name")
    private String lName;
    
    @Column(name="avt_url")
    private String avtUrl;
    
    @Column(name="dob")
    private LocalDate dob;
    
    @Column(name="p_num")
    private String pNum;
    
    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name="gender")
    private Gender gender;
    
    @Column(name="acc_status")
    private AccountStatus accStatus;
}

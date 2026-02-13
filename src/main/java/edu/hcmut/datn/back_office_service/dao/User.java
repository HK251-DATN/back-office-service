package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.AccountStatus;
import edu.hcmut.datn.back_office_service.common.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class User {

    @Column(name="user_id")
    @Id
    @Setter
    @Getter
    private Long userId;

    @Column(name="email")
    @Setter
    @Getter
    private String email;

    @Column(name="f_name")
    @Setter
    @Getter
    private String fName;

    @Column(name="l_name")
    @Setter
    @Getter
    private String lName;

    @Column(name="avt_url")
    @Setter
    @Getter
    private String avtUrl;

    @Column(name="dob")
    @Setter
    @Getter
    private LocalDate dob;

    @Column(name="p_num")
    @Setter
    @Getter
    private String pNum;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="gender")
    @Setter
    @Getter
    private Gender gender;

    @Column(name="acc_status")
    @Setter
    @Getter
    private AccountStatus accStatus;
}

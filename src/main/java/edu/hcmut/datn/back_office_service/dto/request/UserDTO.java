package edu.hcmut.datn.back_office_service.dto.request;

import java.time.LocalDate;

import edu.hcmut.datn.back_office_service.common.enums.AccountStatus;
import edu.hcmut.datn.back_office_service.common.enums.Gender;
import edu.hcmut.datn.back_office_service.dao.User;

public class UserDTO {
    private Long userId;

    private String email;

    private String fName;

    private String lName;

    private String avtUrl;

    private LocalDate dob;

    private String pNum;

    private Gender gender;

    private AccountStatus accStatus;

    public User toEntity() {
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

        return user;
    }

}

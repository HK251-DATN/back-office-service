package edu.hcmut.datn.back_office_service.messaging.user;

import java.time.LocalDate;

import edu.hcmut.datn.back_office_service.common.enums.AccountStatus;
import edu.hcmut.datn.back_office_service.common.enums.Bank;
import edu.hcmut.datn.back_office_service.common.enums.Gender;
import edu.hcmut.datn.back_office_service.dao.Provider;
import edu.hcmut.datn.back_office_service.dao.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCreatedEvent {

    private Long userId;
    private String email;
    private String fName;
    private String lName;
    private String avtUrl;
    private LocalDate dob;
    private String pNum;
    private Gender gender;
    private Bank bankId;
    private String bankNum;
    private boolean isFreshAccount;

    public User toUserEntity() {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setFName(fName);
        user.setLName(lName);
        user.setAvtUrl(avtUrl);
        user.setDob(dob);
        user.setPNum(pNum);
        user.setGender(gender);
        user.setAccStatus(AccountStatus.ACTIVE);
        return user;
    }

    public Provider toProviderEntity() {
        return new Provider(bankId, bankNum, userId);
    }
}

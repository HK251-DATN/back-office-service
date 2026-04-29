package edu.hcmut.datn.back_office_service.dto.response;

import java.time.LocalDate;

import edu.hcmut.datn.back_office_service.common.enums.AccountStatus;
import edu.hcmut.datn.back_office_service.common.enums.Bank;
import edu.hcmut.datn.back_office_service.common.enums.Gender;
import edu.hcmut.datn.back_office_service.common.enums.VerificationMethod;
import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import edu.hcmut.datn.back_office_service.dao.Provider;
import edu.hcmut.datn.back_office_service.dao.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDetailResponse {

    // Provider fields
    private Long providerId;
    private VerificationStatus verificationStatus;
    private VerificationMethod verificationMethod;
    private Long reputationPoint;
    private Bank bankId;
    private String bankNum;

    // User fields
    private Long userId;
    private String email;
    private String fName;
    private String lName;
    private String avtUrl;
    private LocalDate dob;
    private String pNum;
    private Gender gender;
    private AccountStatus accStatus;

    public static ProviderDetailResponse from(Provider provider, User user) {
        return ProviderDetailResponse.builder()
                .providerId(provider.getProviderId())
                .verificationStatus(provider.getVerificationStatus())
                .verificationMethod(provider.getVerificationMethod())
                .reputationPoint(provider.getReputationPoint())
                .bankId(provider.getBankId())
                .bankNum(provider.getBankNum())
                .userId(user.getUserId())
                .email(user.getEmail())
                .fName(user.getFName())
                .lName(user.getLName())
                .avtUrl(user.getAvtUrl())
                .dob(user.getDob())
                .pNum(user.getPNum())
                .gender(user.getGender())
                .accStatus(user.getAccStatus())
                .build();
    }
}

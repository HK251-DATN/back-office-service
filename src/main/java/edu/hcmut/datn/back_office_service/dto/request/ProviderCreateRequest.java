package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.common.enums.Bank;
import edu.hcmut.datn.back_office_service.common.enums.VerificationMethod;
import edu.hcmut.datn.back_office_service.dao.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProviderCreateRequest {

    private Bank bankId;

    private String bankNum;

    private Long userId;

    private VerificationMethod verificationMethod;

    public Provider toEntity() {
        Provider provider = new Provider(
                bankId,
                bankNum,
                userId);
        provider.setVerificationMethod(verificationMethod);
        return provider;
    }
}

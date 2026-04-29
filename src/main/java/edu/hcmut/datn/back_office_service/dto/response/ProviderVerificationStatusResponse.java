package edu.hcmut.datn.back_office_service.dto.response;

import java.util.List;

import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import edu.hcmut.datn.back_office_service.dao.ProviderCertificate;
import edu.hcmut.datn.back_office_service.dao.ProviderVerificationVideo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderVerificationStatusResponse {

    private Long providerId;
    private VerificationStatus verificationStatus;
    private List<ProviderCertificate> certificates;
    private List<ProviderVerificationVideo> videos;
}

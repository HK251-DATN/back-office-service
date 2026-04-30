package edu.hcmut.datn.back_office_service.messaging.provider;

import edu.hcmut.datn.back_office_service.common.enums.CertificateType;
import edu.hcmut.datn.back_office_service.common.enums.VerificationMethod;
import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;

public record ProviderVerificationUpdatedEvent(
        Long providerId,
        VerificationStatus verificationStatus,
        VerificationMethod verificationMethod,
        CertificateType certificateType,  // Nullable - only set for CERTIFICATE method
        String logoUrl  // Provider logo URL
) {}

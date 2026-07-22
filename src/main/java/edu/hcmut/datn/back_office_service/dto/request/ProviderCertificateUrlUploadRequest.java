package edu.hcmut.datn.back_office_service.dto.request;

import java.time.LocalDate;

import edu.hcmut.datn.back_office_service.common.enums.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCertificateUrlUploadRequest {

    private CertificateType certificateType;

    private String certificateNumber;

    private String issuingAuthority;

    private LocalDate issuedDate;

    private LocalDate expiryDate;

    private String documentUrl;
}

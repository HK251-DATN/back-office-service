package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.common.enums.PaymentProvider;
import edu.hcmut.datn.back_office_service.common.enums.PaymentType;
import edu.hcmut.datn.back_office_service.dao.PaymentMethod;

public class PaymentMethodDTO {

    private final PaymentType paymentType;
    private final PaymentProvider paymentProvider;
    private final String accountNum;
    private final Boolean isActive;
    private final Boolean isDefault;
    private final Long buyerId;

    public PaymentMethodDTO(PaymentType paymentType, PaymentProvider paymentProvider, String accountNum, Boolean isActive, Boolean isDefault, Long buyerId) {
        this.paymentType = paymentType;
        this.paymentProvider = paymentProvider;
        this.accountNum = accountNum;
        this.isActive = isActive;
        this.isDefault = isDefault;
        this.buyerId = buyerId;
    }

    public PaymentMethod toEntity() {
        PaymentMethod paymentMethod = new PaymentMethod();

        if (paymentType != null) {
            paymentMethod.setPaymentType(paymentType);
        }

        if (paymentProvider != null) {
            paymentMethod.setPaymentProvider(paymentProvider);
        }

        if (!accountNum.isBlank()) {
            paymentMethod.setAccountNum(accountNum);
        }

        if (isDefault != null) {
            paymentMethod.setIsDefault(isDefault);
        }

        if (isActive != null) {
            paymentMethod.setIsActive(isActive);
        }

        if (buyerId != null) {
            paymentMethod.setBuyerId(buyerId);
        }

        return paymentMethod;
    }
}

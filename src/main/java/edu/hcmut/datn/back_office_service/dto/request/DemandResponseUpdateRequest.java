package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.common.enums.DemandResponseStatus;
import edu.hcmut.datn.back_office_service.dao.DemandResponse;

public class DemandResponseUpdateRequest {
    private DemandResponseStatus status;

    public DemandResponse toEntity() {
        return new DemandResponse(status);
    }

}

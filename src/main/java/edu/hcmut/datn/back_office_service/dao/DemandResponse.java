package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.DemandResponseStatus;
import edu.hcmut.datn.back_office_service.common.enums.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class DemandResponse {

    @Column(name = "demand_resp_id")
    @Id
    private Long demandRespId;

    @Column(name = "status")
    private DemandResponseStatus status;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "unit")
    private Unit unit;

    @Column(name = "createdAt")
    private LocalDateTime created_at;

    @Column(name = "updatedAt")
    private LocalDateTime updated_at;

    @Column(name = "prodRqstId")
    private Long prod_rqst_id;

    @Column(name = "providerId")
    private Long provider_id;

}

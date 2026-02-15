package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.DemandResponseStatus;
import edu.hcmut.datn.back_office_service.common.enums.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class DemandResponse {

    @Column(name = "demand_resp_id")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Getter
    private Long demandRespId;

    @Column(name = "status")
    @Getter
    @Setter
    private DemandResponseStatus status;

    @Column(name = "quantity")
    @Getter
    private Long quantity;

    @Column(name = "unit")
    @Getter
    private Unit unit;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "prodRqstId")
    @Getter
    private Long prodRqstId;

    @Column(name = "providerId")
    @Getter
    private Long providerId;

    protected DemandResponse() {}

    public DemandResponse(
            DemandResponseStatus status,
            Long quantity,
            Unit unit,
            Long prodRqstId,
            Long providerId
    ) {
        this.status     = status;
        this.quantity   = quantity;
        this.unit       = unit;
        this.prodRqstId = prodRqstId;
        this.providerId = providerId;
    }

    public DemandResponse(
            DemandResponseStatus status
    ) {
        this.status     = status;
    }

}

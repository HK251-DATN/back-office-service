package edu.hcmut.datn.back_office_service.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import edu.hcmut.datn.back_office_service.dao.SaleEvent;

public class SaleEventCreateRequest {
    private String name;
    private String description;
    private String img;
    private Long displayPriority;
    private Boolean isActive;
    private LocalDate beginDate;
    private LocalDate endDate;
    private LocalTime beginTime;
    private LocalTime endTime;
    private Long eventId;

    public SaleEvent toEntity() {
        return new SaleEvent(name, description, img, displayPriority, isActive, beginDate, endDate, beginTime, endTime,
                eventId);
    }
}

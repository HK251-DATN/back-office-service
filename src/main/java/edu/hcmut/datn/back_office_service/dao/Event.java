package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import org.springframework.boot.jackson.autoconfigure.JacksonProperties.Json;

import edu.hcmut.datn.back_office_service.common.enums.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Event {
    
    @Column(name="event_id")
    @Id
    private Long eventId;
    
    @Column(name="event_type")
    private EventType eventType;
    
    @Column(name="cron_exp")
    private String cronExp;
    
    @Column(name="begin_time")
    private LocalDateTime beginTime;
    
    @Column(name="end_time")
    private LocalDateTime endTime;
    
    @Column(name="is_active")
    private Boolean isActive;
    
    @Column(name="last_trigger")
    private LocalDateTime lastTrigger;
    
    @Column(name="next_trigger")
    private LocalDateTime nextTrigger;
    
    @Column(name="event_payload")
    private Json eventPayload;
    
    @Column(name="created_at")
    private Long createdAt;
    
    @Column(name="updated_at")
    private Long updatedAt;
    
    @Column(name="created_by")
    private Long createdBy;
}

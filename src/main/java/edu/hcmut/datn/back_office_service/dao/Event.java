package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import edu.hcmut.datn.back_office_service.common.enums.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "events")
@NoArgsConstructor
public class Event {

    @Column(name = "event_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long eventId;

    @Column(name = "event_type")
    @Getter
    private EventType eventType;

    @Column(name = "cron_exp")
    @Getter
    @Setter
    private String cronExp;

    @Column(name = "begin_time")
    @Getter
    private LocalDateTime beginTime;

    @Column(name = "end_time")
    @Getter
    @Setter
    private LocalDateTime endTime;

    @Column(name = "is_active")
    @Getter
    @Setter
    private Boolean isActive;

    @Column(name = "last_trigger")
    @Getter
    private LocalDateTime lastTrigger;

    @Column(name = "next_trigger")
    @Getter
    private LocalDateTime nextTrigger;

    @Column(name = "event_payload")
    @JdbcTypeCode(SqlTypes.JSON)
    @Getter
    @Setter
    private JsonNode eventPayload;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    @Getter
    private Long createdBy;

    // Constructor for creating purpose
    public Event(EventType eventType, String cronExp, LocalDateTime beginTime, LocalDateTime endTime, Boolean isActive,
            LocalDateTime lastTrigger, LocalDateTime nextTrigger, JsonNode eventPayload, Long createdBy) {
        this.eventType = eventType;
        this.cronExp = cronExp;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.isActive = isActive;
        this.lastTrigger = lastTrigger;
        this.nextTrigger = nextTrigger;
        this.eventPayload = eventPayload;
        this.createdBy = createdBy;
    }

    // Constructor for updating purpose
    public Event(String cronExp, LocalDateTime endTime, Boolean isActive, JsonNode eventPayload) {
        this.cronExp = cronExp;
        this.endTime = endTime;
        this.isActive = isActive;
        this.eventPayload = eventPayload;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // Set createdAt on first save
        updatedAt = LocalDateTime.now(); // Optional: Set initial updatedAt
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(); // Update on every save after creation
    }
}

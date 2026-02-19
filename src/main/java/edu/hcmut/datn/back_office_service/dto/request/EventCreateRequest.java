package edu.hcmut.datn.back_office_service.dto.request;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.EventType;
import edu.hcmut.datn.back_office_service.dao.Event;
import tools.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {
    private EventType eventType;
    private String cronExp;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    private LocalDateTime lastTrigger;
    private LocalDateTime nextTrigger;
    private JsonNode eventPayload;
    private Long createdBy;

    public Event toEntity() {
        return new Event(eventType, cronExp, beginTime, endTime, isActive, lastTrigger, nextTrigger, eventPayload,
                createdBy);
    }
}

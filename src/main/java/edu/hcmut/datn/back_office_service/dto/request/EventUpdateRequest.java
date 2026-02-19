package edu.hcmut.datn.back_office_service.dto.request;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.dao.Event;
import tools.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest {
    private String cronExp;
    private LocalDateTime endTime;
    private Boolean isActive;
    private JsonNode eventPayload;

    public Event toEntity() {
        return new Event(cronExp, endTime, isActive, eventPayload);
    }
}

package edu.hcmut.datn.back_office_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.hcmut.datn.back_office_service.dao.Event;
import edu.hcmut.datn.back_office_service.dto.response.ApiResponse;
import edu.hcmut.datn.back_office_service.service.EventService;

@Controller
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Event>> create(@RequestBody Event event) {
        try {
            Event newEvent = eventService.create(event);

            return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Create event successfully", newEvent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Event>> read(@PathVariable Long eventId) {
        try {
            Event event = eventService.read(eventId);

            return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Read event successfully", event));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Event>>> readAll(Integer pageNum, Integer pageSize) {
        List<Event> events = eventService.readAll(pageNum, pageSize);

        if (events.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No Event Exists", null));
        }

        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get all events successfully", events));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Event>> update(@PathVariable Long eventId, @RequestBody Event event) {
        try {
            Event updated = eventService.update(eventId, event);

            return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Update event successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long eventId) {
        try {
            eventService.delete(eventId);

            return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Delete event successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

}

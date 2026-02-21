package edu.hcmut.datn.back_office_service.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.hcmut.datn.back_office_service.dao.Event;
import edu.hcmut.datn.back_office_service.exception.event.EventNotFoundException;
import edu.hcmut.datn.back_office_service.repository.EventRepository;
import edu.hcmut.datn.back_office_service.service.EventService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event create(Event event) {
        try {
            return eventRepository.save(event);
        } catch (Exception e) {
            log.info("Error message: {}", e.getMessage());

            return null;
        }
    }

    @Override
    public Event read(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event Not Found"));
    }

    @Override
    public List<Event> readAll(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        Page<Event> events = eventRepository.findAll(pageable);

        return events.toList();
    }

    @Override
    public Event update(Long eventId, Event event) {
        Event cur = read(eventId);

        if (event.getCronExp() != null) {
            cur.setCronExp(event.getCronExp());
        }

        if (event.getEndTime() != null) {
            cur.setEndTime(event.getEndTime());
        }

        if (event.getIsActive() != null) {
            cur.setIsActive(event.getIsActive());
        }

        return eventRepository.save(cur);
    }

    @Override
    public void delete(Long eventId) {
        eventRepository.delete(read(eventId));
    }
}

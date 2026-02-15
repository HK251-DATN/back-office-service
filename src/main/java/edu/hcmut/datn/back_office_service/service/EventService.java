package edu.hcmut.datn.back_office_service.service;

import java.util.List;

import edu.hcmut.datn.back_office_service.dao.Event;

public interface EventService {
    Event create(Event event);

    Event read(Long eventId);

    List<Event> readAll(Integer pageNum, Integer pageSize);

    Event update(Long eventId, Event event);

    void delete(Long eventId);
}

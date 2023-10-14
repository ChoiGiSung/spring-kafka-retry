package com.example.retry.event

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EventService(
    private val eventRepository: EventRepository
) {

    @Transactional
    fun save(event: Event) {
        eventRepository.save(event)
    }

    fun findAll(): List<Event> {
        return eventRepository.findAll()
    }
}
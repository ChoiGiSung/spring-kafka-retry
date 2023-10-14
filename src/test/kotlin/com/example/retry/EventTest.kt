package com.example.retry

import com.example.retry.event.Event
import com.example.retry.event.EventService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EventTest {

    @Autowired
    lateinit var eventService: EventService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun saveAndFindAll() {
        eventService.save(
            Event(
                payload = objectMapper.writeValueAsString(
                    CustomProducer.UserInfo(
                        "홍길동",
                        "010-1234-5678",
                        "김김"
                    )
                )
            )
        )

        println("event: ${eventService.findAll()}")
    }
}
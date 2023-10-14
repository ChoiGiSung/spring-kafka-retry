package com.example.retry

import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import java.util.concurrent.TimeoutException
@Component
class ConsumerController {

    @KafkaListener(groupId = "sample-1", topics = ["testTopic"])
    @RetryableTopic(
        backoff = Backoff(value = 3000L),
        attempts = "4",
        autoCreateTopics = "true", //default
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, // -0, -1 suffix
    )
    fun consume(user: User) {
        when (user) {
            is UserInfo -> println("user: $user")
            is OtherUserInfo -> println("other user: $user")
        }
        throw NullPointerException("Exception!")
    }

    @DltHandler
    fun handleDlt(event: Any) {
        println("dlt event: $event")
    }

    interface User

    data class UserInfo(
        val name: String,
        val phoneNumber: String
    ): User

    data class OtherUserInfo(
        val address: String,
        val age: Int
    ): User
}
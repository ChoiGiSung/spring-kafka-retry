package com.example.retry

import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@KafkaListener(groupId = "sample-1", topics = ["testTopic"])
class ConsumerController {

    @KafkaHandler
    fun consume(userInfo: UserInfo) {
        println(userInfo)
    }

    @KafkaHandler
    fun consume(userInfo: OtherUserInfo) {
        println(userInfo)
    }

    data class UserInfo(
        val name: String,
        val phoneNumber: String
    )

    data class OtherUserInfo(
        val address: String,
        val age: Int
    )
}
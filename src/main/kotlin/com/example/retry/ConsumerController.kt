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
@KafkaListener(groupId = "sample-1", topics = ["testTopic"])
class ConsumerController {

    //todo test
    @KafkaHandler
    @RetryableTopic(
        backoff = Backoff(value = 3000L),
        attempts = "4",
        autoCreateTopics = "true", //default
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, // -0, -1 suffix
        dltStrategy = DltStrategy.FAIL_ON_ERROR, // dlt 전송 전략
        include = [TimeoutException::class], //리트라이함
        exclude = [NullPointerException::class] //리트라이 안함
    )
    fun consume(userInfo: UserInfo) {
        println(userInfo)
    }

    @KafkaHandler
    fun consume(userInfo: OtherUserInfo) {
        println(userInfo)
    }

    @DltHandler
    fun handleDlt(event: Any) {
        println("dlt event: $event")
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
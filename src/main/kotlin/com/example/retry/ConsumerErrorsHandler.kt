package com.example.retry

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header

class ConsumerErrorsHandler {

    fun postProcessDltMessage(
        record: ConsumerRecord<String?, String?>,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String?,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partitionId: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long?,
        @Header(KafkaHeaders.EXCEPTION_MESSAGE) errorMessage: String?,
        @Header(KafkaHeaders.GROUP_ID) groupId: String?
    ) {
        println(
            "DLT event:'${record.value()}' with partitionId='${partitionId}', offset='${offset}', topic='${topic}', groupId='${groupId}', errorMessage='${errorMessage}'",
        )
    }
}
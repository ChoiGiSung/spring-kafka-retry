package com.example.retry

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.retrytopic.RetryTopicConfiguration
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.kafka.support.EndpointHandlerMethod
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer


@Configuration
class KafkaConfig(
    private val objectMapper: ObjectMapper
) : DefaultKafkaConsumerFactoryCustomizer {

    @Bean
    fun retryTopicConfig(
        kafkaTemplate: KafkaTemplate<String, Any>,
        concurrentKafkaListenerContainerFactory: ConcurrentKafkaListenerContainerFactory<String, Any>
    ): RetryTopicConfiguration {
        return RetryTopicConfigurationBuilder
            .newInstance()
            .autoCreateTopics(true, 1, 1)
            .maxAttempts(5)
            .fixedBackOff(3000L)
            .retryTopicSuffix("-coco-retry")
            .dltSuffix("-coco-dlt")
            .listenerFactory(concurrentKafkaListenerContainerFactory)
            .setTopicSuffixingStrategy(TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
            .dltHandlerMethod(
                EndpointHandlerMethod(
                    ConsumerErrorsHandler::class.java,
                    ConsumerErrorsHandler::postProcessDltMessage.name
                )
            )
            .create(kafkaTemplate)
    }

    override fun customize(consumerFactory: DefaultKafkaConsumerFactory<*, *>) {
        (consumerFactory as DefaultKafkaConsumerFactory<String, Any>).setValueDeserializer(
            errorHandlingDeserializer()
        )
    }

    private fun errorHandlingDeserializer(): ErrorHandlingDeserializer<Any> {
        val errorHandlingDeserializer = ErrorHandlingDeserializer<Any>(
            JsonDeserializer<Any>(
                objectMapper
            )
        )
        return errorHandlingDeserializer
    }

}
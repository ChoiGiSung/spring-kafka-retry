package com.example.retry

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.header.Header
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
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import java.nio.charset.StandardCharsets


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

        errorHandlingDeserializer.setFailedDeserializationFunction {
            val headerIterator: Iterator<Header> =
                it.headers.headers(DefaultJackson2JavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME).iterator()
            if (!headerIterator.hasNext()) {
                return@setFailedDeserializationFunction null
            }
            val token = String(headerIterator.next().value(), StandardCharsets.UTF_8)
            return@setFailedDeserializationFunction if (token == "sample") null else "do something"
        }
        return errorHandlingDeserializer
    }


}
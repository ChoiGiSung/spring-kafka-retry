package com.example.retry

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.retrytopic.RetryTopicConfiguration
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.kafka.support.EndpointHandlerMethod



@Configuration
class KafkaConfig(
) {

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
            .dltHandlerMethod(EndpointHandlerMethod(ConsumerErrorsHandler::class.java, ConsumerErrorsHandler::postProcessDltMessage.name))
            .create(kafkaTemplate)
    }

}
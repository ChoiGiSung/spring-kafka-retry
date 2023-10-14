package com.example.retry

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class KafkaProducerTest {

	@Autowired
	lateinit var producer: CustomProducer

	@Test
	fun something() {
		producer.send(CustomProducer.UserInfo("홍길동", "010-1234-5678", "홍홍"))
	}

	@Test
	fun something2() {
		producer.send(CustomProducer.OtherUserInfo("홍길동", 12))
	}

	@Test
	fun something3() {
		producer.sendToRetryTopic(CustomProducer.UserInfo("홍길동", "010-1234-5678", "홍홍"))
	}
}

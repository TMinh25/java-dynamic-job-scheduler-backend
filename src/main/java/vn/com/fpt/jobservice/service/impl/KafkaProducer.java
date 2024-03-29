package vn.com.fpt.jobservice.service.impl;

import com.fpt.framework.kafka.producer.annotation.ReactiveKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;
import vn.com.fpt.jobservice.model.request.KafkaMessageRequest;

@Service
@Slf4j
public class KafkaProducer {
    @Value("${kafka.enabled}")
    private Boolean useKafka;
    @Value("${kafka.topics.producerJobServiceTopic}")
    private String producerJobServiceTopic;
    @ReactiveKafkaProducer
    private ReactiveKafkaProducerTemplate<String, Object> kafkaProducerTemplate;

    public Mono<SenderResult<Void>> sendKafkaMessage(KafkaMessageRequest messageRequest, String tenantId) {
        if (useKafka) {
            String topic = producerJobServiceTopic + "_" + tenantId;
            log.info("Sending Kafka message: Topic: {}, Message: {}", topic, messageRequest);
            return kafkaProducerTemplate.send(topic, messageRequest);
        }
        log.debug("Kafka is disabled. Not sending message.");
        return Mono.empty();
    }
}

package config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class consumer {

    @KafkaListener(topics = "test", groupId = "test-group")
    public void consumeMessage(String message) {
        log.info("Received test message from Kafka: {}", message);
    }
}


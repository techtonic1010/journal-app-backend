package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class producer {
@Autowired
private KafkaTemplate kafkaTemplate;

@GetMapping("/send")
public void sendMessage(@RequestParam String message){
    kafkaTemplate.send("test" , message);
}
}

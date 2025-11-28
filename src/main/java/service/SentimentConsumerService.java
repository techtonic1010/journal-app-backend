package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.SentimentAnalysisEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SentimentConsumerService {
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "sentiment-analysis", groupId = "journal-app-group")
    public void consumeSentimentEvent(String eventJson) {
        try {
            SentimentAnalysisEvent event = objectMapper.readValue(eventJson, SentimentAnalysisEvent.class);
            
            log.info("Received sentiment event from Kafka for user: {}, sentiment: {}", 
                    event.getUserEmail(), event.getSentiment());
            
            sendEmail(event);
            
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize sentiment event: {}", eventJson, e);
        } catch (Exception e) {
            log.error("Error processing sentiment event: {}", eventJson, e);
        }
    }

    private void sendEmail(SentimentAnalysisEvent event) {
        String subject = "Your Weekly Sentiment Analysis";
        String body = String.format(
                "Hello,\n\n" +
                "Here's your sentiment analysis for the last 7 days:\n\n" +
                "Most Frequent Sentiment: %s\n" +
                "Number of Entries: %d\n\n" +
                "Keep journaling!\n\n" +
                "Best regards,\n" +
                "Journal App Team",
                event.getSentiment(),
                event.getEntryCount()
        );
        
        emailService.sendmail(event.getUserEmail(), subject, body);
        log.info("Sentiment email sent successfully to: {}", event.getUserEmail());
    }
}


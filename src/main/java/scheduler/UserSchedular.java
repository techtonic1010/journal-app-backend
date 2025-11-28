package scheduler;

import Sentiment.Sentiment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.SentimentAnalysisEvent;
import entities.JournalEntry;
import entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import repositary.UserRepositoryImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserSchedular {

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SENTIMENT_TOPIC = "sentiment-analysis";

    @Scheduled(cron = "0/30 * * * * ?")
    public void fetchUsersAndSendMails() {
        List<User> users = userRepositoryImpl.getforSA();
        log.info("Scheduled sentiment analysis started for {} users", users.size());

        for (User user : users) {
            try {
                List<JournalEntry> journalEntries = user.getJournalEntries();
                
                // Filter entries from last 7 days
                List<Sentiment> sentiments = journalEntries.stream()
                        .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                        .map(JournalEntry::getSentiment)
                        .collect(Collectors.toList());

                // Count sentiment occurrences
                Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
                for (Sentiment sentiment : sentiments) {
                    if (sentiment != null) {
                        sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
                    }
                }

                // Find most frequent sentiment
                Sentiment mostFrequentSentiment = null;
                int maxCount = 0;
                for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        maxCount = entry.getValue();
                        mostFrequentSentiment = entry.getKey();
                    }
                }

                // Publish to Kafka if sentiment found
                // here we create an event 
                if (mostFrequentSentiment != null) {
                    SentimentAnalysisEvent event = new SentimentAnalysisEvent(
                            user.getEmail(),
                            mostFrequentSentiment,
                            maxCount
                    );
                    
                    String eventJson = objectMapper.writeValueAsString(event);
                    kafkaTemplate.send(SENTIMENT_TOPIC, eventJson);
                    
                    log.info("Published sentiment event to Kafka for user: {}, sentiment: {}", 
                            user.getEmail(), mostFrequentSentiment);
                }
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize sentiment event for user: {}", user.getEmail(), e);
            } catch (Exception e) {
                log.error("Error processing sentiment for user: {}", user.getEmail(), e);
            }
        }
    }
}

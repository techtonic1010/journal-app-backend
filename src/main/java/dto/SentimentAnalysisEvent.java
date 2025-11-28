package dto;

import Sentiment.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysisEvent {
    private String userEmail;
    private Sentiment sentiment;
    private int entryCount;
}

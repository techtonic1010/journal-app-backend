package dto;

import Sentiment.Sentiment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Journal entry creation/update request")
public class JournalEntryDTO {
    
    @Schema(description = "Title of the journal entry", example = "My Amazing Day", required = true)
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @Schema(description = "Content/body of the journal entry", example = "Today was a wonderful day. I learned so much about Spring Boot and MongoDB!", required = true)
    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content;
    
    @Schema(description = "Sentiment/mood of the entry", example = "HAPPY", allowableValues = {"HAPPY", "SAD", "ANGRY", "EXCITED", "NEUTRAL"})
    private Sentiment sentiment;
}

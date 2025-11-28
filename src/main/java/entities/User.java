package entities;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor   // 👈 required with @Builder
@Document(collection = "users")
@NoArgsConstructor
public class User {
    @Id  // ← Critical for auto-generating _id
    private String id;
    // unique username chahiye
    @Indexed(unique = true)
    @NonNull
    private String userName ;
    private String email;
    private boolean sentimentAnalysis;
    @NonNull
    private String password;

//    [[[[
//    User user = userService.findByUserName(userName);
//    JournalEntry saved = journalEntryRepository.save(journalEntry);
//user.getJournalEntries().add(saved);
//userService.saveEntry(user);   // <- this saves the DBRef link
//]]]]
    @DBRef
    private List<JournalEntry> journalEntries = new ArrayList<>();

    private List<String> roles;

}

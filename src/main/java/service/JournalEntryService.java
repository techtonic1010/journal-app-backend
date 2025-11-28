package service;
import Sentiment.Sentiment;
import entities.JournalEntry;
import entities.User;
import exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import repositary.JournalEntryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JournalEntryService {
    //implementation ka object runtime pe
    // serivce mei daal dega
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    public void saveEntry(JournalEntry journalEntry , String userName){
        User user = userService.findByUserName(userName);
        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + userName);
        }
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepository.save(journalEntry);
        user.getJournalEntries().add(saved);
        userService.saveEntry(user);
    }

    public List<JournalEntry> getAllEntries() {
        return journalEntryRepository.findAll();
    }

//    public Optional< JournalEntry> findbyId(String id){
//        return journalEntryRepository.findById((String) id);
//    }

    public void deleteById (String id, User user){
        boolean removed = user.getJournalEntries().removeIf( x -> x.getId().equals(id));
        if(removed){
            userService.saveEntry(user);
            journalEntryRepository.deleteById(String.valueOf((id)));
        } else {
            throw new ResourceNotFoundException("Journal entry not found with id: " + id);
        }
    }

    public Optional<JournalEntry> findById(String id) {
        return journalEntryRepository.findById(id);
    }

    public Page<JournalEntry> findEntriesWithFilters(
            String userName,
            Sentiment sentiment,
            String searchKeyword,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        User user = userService.findByUserName(userName);
        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + userName);
        }

        // Get user's journal entry IDs
        List<String> entryIds = user.getJournalEntries()
                .stream()
                .map(JournalEntry::getId)
                .collect(Collectors.toList());

        // Convert LocalDate to LocalDateTime for MongoDB query
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

        log.info("Fetching entries for user: {} with filters - sentiment: {}, search: {}, dates: {} to {}",
                userName, sentiment, searchKeyword, startDate, endDate);

        return journalEntryRepository.findByUserEntriesWithFilters(
                entryIds,
                sentiment,
                startDateTime,
                endDateTime,
                searchKeyword,
                pageable
        );
    }

//    public boolean existsById(String id) {
//        return journalEntryRepository.existsById(id);
//    }
}

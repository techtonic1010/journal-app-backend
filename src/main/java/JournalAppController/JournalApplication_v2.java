package JournalAppController;
import Sentiment.Sentiment;
import dto.JournalEntryDTO;
import entities.JournalEntry;
import entities.User;
import exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import service.JournalEntryService;
import service.UserService;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/journal")
@Tag(name = "Journal APIs", description = "CRUD operations for journal entries with pagination, filtering, and sentiment analysis (requires JWT authentication)")
@SecurityRequirement(name = "Bearer Authentication")
public class JournalApplication_v2 {

//    private final Map<Long, JournalEntry> journalEntries = new HashMap<>();
    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    // samaj lo ki hamare pass , username aa raha hai ,, us username se , hume , user mil gya
    // using findbyUsername karke.

    // get the entries of our authenticaed user.
    @Operation(
            summary = "Get paginated journal entries with filters",
            description = "Retrieve journal entries for the authenticated user with pagination and optional filters (sentiment, date range, keyword search). Results are sorted by date (newest first)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved journal entries",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<JournalEntry>> getAllJournalEntriesOfUser(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Filter by sentiment (HAPPY, SAD, ANGRY, etc.)", example = "HAPPY")
            @RequestParam(required = false) Sentiment sentiment,
            
            @Parameter(description = "Search keyword in title or content", example = "vacation")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Start date for filtering (YYYY-MM-DD)", example = "2025-01-01")
            @RequestParam(required = false) LocalDate startDate,
            
            @Parameter(description = "End date for filtering (YYYY-MM-DD)", example = "2025-12-31")
            @RequestParam(required = false) LocalDate endDate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        // Create pageable with sorting by date (newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        Page<JournalEntry> entries = journalEntryService.findEntriesWithFilters(
                userName, sentiment, search, startDate, endDate, pageable
        );

        log.info("Retrieved {} entries for user: {} (page: {}, size: {})",
                entries.getNumberOfElements(), userName, page, size);

        return ResponseEntity.ok(entries);
    }

    @Operation(
            summary = "Create a new journal entry",
            description = "Create a new journal entry for the authenticated user. The entry date is automatically set to the current timestamp."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Journal entry created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JournalEntry.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@Valid @RequestBody JournalEntryDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        
        // Convert DTO to Entity
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setTitle(dto.getTitle());
        journalEntry.setContent(dto.getContent());
        journalEntry.setSentiment(dto.getSentiment());
        
        journalEntryService.saveEntry(journalEntry, userName);
        return new ResponseEntity<>(journalEntry, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get journal entry by ID",
            description = "Retrieve a specific journal entry by its ID. Only the owner can access their entries (ownership verification applied)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journal entry found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JournalEntry.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Journal entry not found or user doesn't own this entry",
                    content = @Content)
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getJournalEntryById(
            @Parameter(description = "Journal entry ID", example = "673c5e8f9b1234567890abcd")
            @PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + userName);
        }

        if (user.getJournalEntries() == null) {
            throw new ResourceNotFoundException("No journal entries found");
        }

        boolean ownsEntry = user.getJournalEntries().stream()
                .anyMatch(entry -> entry.getId().toString().equals(id));

        if (!ownsEntry) {
            throw new ResourceNotFoundException("Journal entry not found with id: " + id);
        }

        return journalEntryService.findById(id)
                .map(entry -> ResponseEntity.ok(entry))
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));
    }

    @Operation(
            summary = "Delete journal entry by ID",
            description = "Delete a specific journal entry. Only the owner can delete their entries."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Journal entry deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Journal entry not found",
                    content = @Content)
    })
    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(
            @Parameter(description = "Journal entry ID to delete", example = "673c5e8f9b1234567890abcd")
            @PathVariable String myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
       journalEntryService.deleteById(myId , user);
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Not found
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Update journal entry by ID",
            description = "Update an existing journal entry. Supports partial updates - only provided fields will be updated. Only the owner can update their entries."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journal entry updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JournalEntry.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Journal entry not found or user doesn't own this entry",
                    content = @Content)
    })
    @PutMapping("/id/{id}")
    public ResponseEntity<JournalEntry> updateJournalById(
            @Parameter(description = "Journal entry ID to update", example = "673c5e8f9b1234567890abcd")
            @PathVariable String id,
            @Valid @RequestBody JournalEntryDTO dto) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + userName);
        }

        // Check ownership: verify the entry belongs to this user
        boolean ownsEntry = user.getJournalEntries() != null &&
                user.getJournalEntries().stream()
                        .anyMatch(entry -> entry.getId().toString().equals(id));

        if (!ownsEntry) {
            throw new ResourceNotFoundException("Journal entry not found with id: " + id);
        }

        // Find the journal entry
        JournalEntry old = journalEntryService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        // Partial update: only update fields that are provided
        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            old.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
            old.setContent(dto.getContent());
        }
        if (dto.getSentiment() != null) {
            old.setSentiment(dto.getSentiment());
        }

        journalEntryService.saveEntry(old, userName);
        log.info("Journal entry updated successfully with id: {} by user: {}", id, userName);
        return new ResponseEntity<>(old, HttpStatus.OK);
    }

/// //////////////////////////////////////////////////////////////////////
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/test-redis")
    public String testRedis() {
        try {
            redisTemplate.opsForValue().set("testKey", "helloRedis");
            String value = redisTemplate.opsForValue().get("testKey");
            return "Redis Test Success! Value = " + value;
        } catch (Exception e) {
            log.error("Redis test failed", e);
            return "Redis Test Failed: " + e.getMessage();
        }
    }
}



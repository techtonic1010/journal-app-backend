package repositary;

import Sentiment.Sentiment;
import entities.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {

    @Query("{ '_id': { $in: ?0 }, " +
           "$and: [ " +
           "  { $or: [ { 'sentiment': ?1 }, { ?1: null } ] }, " +
           "  { $or: [ { 'date': { $gte: ?2 } }, { ?2: null } ] }, " +
           "  { $or: [ { 'date': { $lte: ?3 } }, { ?3: null } ] }, " +
           "  { $or: [ " +
           "    { 'title': { $regex: ?4, $options: 'i' } }, " +
           "    { 'content': { $regex: ?4, $options: 'i' } }, " +
           "    { ?4: null } " +
           "  ] } " +
           "] }")
    Page<JournalEntry> findByUserEntriesWithFilters(
            @Param("entryIds") java.util.List<String> entryIds,
            @Param("sentiment") Sentiment sentiment,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable
    );
}

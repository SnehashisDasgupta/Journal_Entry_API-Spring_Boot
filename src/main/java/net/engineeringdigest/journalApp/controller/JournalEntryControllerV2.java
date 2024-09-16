package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<JournalEntry> allEntries = journalEntryService.getAll();
        if (allEntries != null && !allEntries.isEmpty()){
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            if (myEntry.getTitle()==null || myEntry.getContent()==null)  return new ResponseEntity<>("Enter title & content field to create new journal entry",HttpStatus.NOT_ACCEPTABLE);

            journalEntryService.saveEntry(myEntry);
            return new ResponseEntity<>("Journal entry created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Error in createEntry: " + e.getMessage());
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
        if (journalEntry.isPresent()) {
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable ObjectId myId) {
        journalEntryService.deleteById(myId);
        return new ResponseEntity<>("Journal entry deleted successfully",HttpStatus.NO_CONTENT);
    }

    @PutMapping("id/{myId}")
    public ResponseEntity<String> updateJournalEntryById(@PathVariable ObjectId myId, @RequestBody JournalEntry givenEntry) {
        try {
            JournalEntry existingEntry = journalEntryService.findById(myId).orElse(null);
            if (existingEntry == null) return new ResponseEntity<>("Journal Entry not found", HttpStatus.NOT_FOUND);

            // if both 'title' and 'content' fields are not given show 'BAD_REQUEST'
            if (givenEntry.getTitle()==null && givenEntry.getContent()==null) return new ResponseEntity<>("Enter a field to update the journal entry", HttpStatus.NOT_ACCEPTABLE);

            // Updating only non-null & non-empty fields from the given entry
            if (givenEntry.getTitle() != null && !givenEntry.getTitle().isEmpty())
                existingEntry.setTitle(givenEntry.getTitle());
            if (givenEntry.getContent() != null && !givenEntry.getContent().isEmpty())
                existingEntry.setContent(givenEntry.getContent());

            journalEntryService.saveEntry(existingEntry);
            return new ResponseEntity<>("Journal entry updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while updating journal entry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

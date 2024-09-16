package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;
    
    @Autowired
    private UserService userService;

    @GetMapping("{username}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        List<JournalEntry> allUserEntries = user.getJournalEntries();
        if (allUserEntries != null && !allUserEntries.isEmpty()){
            return new ResponseEntity<>(allUserEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("{username}")
    public ResponseEntity<String> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String username) {
        try {
            journalEntryService.saveEntry(myEntry, username);
            return new ResponseEntity<>("Journal entry created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Error in createEntry: " + e.getMessage());
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId id) {
        Optional<JournalEntry> journalEntry = journalEntryService.findById(id);
        if (journalEntry.isPresent()) {
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{username}/{id}")
    public ResponseEntity<String> deleteJournalEntry(@PathVariable String username,@PathVariable ObjectId id) {
        if (!journalEntryService.findById(id).isPresent()) {
            return new ResponseEntity<>("Journal entry doesn't exist", HttpStatus.NOT_FOUND);
        }

        journalEntryService.deleteById(username, id); // delete journal entry from JournalEntry entity
        return new ResponseEntity<>("Journal entry deleted successfully",HttpStatus.OK);
    }

    @PutMapping("id/{username}/{id}")
    public ResponseEntity<String> updateJournalEntryById(@PathVariable String username, @PathVariable ObjectId id, @RequestBody JournalEntry givenEntry) {
        try {
            JournalEntry existingEntry = journalEntryService.findById(id).orElse(null);
            if (existingEntry == null) return new ResponseEntity<>("Journal Entry not found", HttpStatus.NOT_FOUND);

            // Updating only non-null & non-empty fields from the given entry
            if (!givenEntry.getTitle().isEmpty())
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

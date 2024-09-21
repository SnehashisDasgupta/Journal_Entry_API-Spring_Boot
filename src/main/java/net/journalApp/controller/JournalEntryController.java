package net.journalApp.controller;

import net.journalApp.entity.JournalEntry;
import net.journalApp.entity.User;
import net.journalApp.service.JournalEntryService;
import net.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/entry")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        // get all journal entries of given user and store in allUserEntries
        List<JournalEntry> allUserEntries = user.getJournalEntries();
        if (allUserEntries != null && !allUserEntries.isEmpty()){
            return new ResponseEntity<>(allUserEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            journalEntryService.saveEntry(myEntry, username);
            return new ResponseEntity<>("Journal entry created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving journal entry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        // find whether the entry ID given is present in the JournalEntry list or not.
        List<JournalEntry> targetEntry = user.getJournalEntries()
                .stream()
                .filter(entry -> entry.getId().equals(id))
                .collect(Collectors.toList());

        // if ID present in targetEntry then return the journalEntry of the given ID
        if (!targetEntry.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(id);
            if (journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{id}")
    public ResponseEntity<String> deleteJournalEntryById (@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (!journalEntryService.findById(id).isPresent()) {
            return new ResponseEntity<>("Journal entry doesn't exist", HttpStatus.NOT_FOUND);
        }
        journalEntryService.deleteById(username, id);
        return new ResponseEntity<>("Successfully journal entry deleted", HttpStatus.OK);
    }

    @PutMapping("id/{id}")
    public ResponseEntity<String> updateJournalEntryById (@PathVariable ObjectId id, @RequestBody JournalEntry givenEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            // find whether the entry ID given is present in the JournalEntry list or not.
            List<JournalEntry> targetEntry = user.getJournalEntries()
                    .stream()
                    .filter(entry -> entry.getId().equals(id))
                    .collect(Collectors.toList());

            // if ID present in targetEntry then return the journalEntry of the given ID
            if (!targetEntry.isEmpty()) {
                Optional<JournalEntry> existingEntryOpt = journalEntryService.findById(id);

                if (existingEntryOpt.isPresent()) {
                    JournalEntry existingEntry = existingEntryOpt.get();  // Get the actual JournalEntry object from the 'Optional'

                    if (!givenEntry.getTitle().isEmpty())
                        existingEntry.setTitle(givenEntry.getTitle());
                    if (givenEntry.getContent() != null && !givenEntry.getContent().isEmpty())
                        existingEntry.setContent(givenEntry.getContent());
                    journalEntryService.saveEntry(existingEntry);
                    return new ResponseEntity<>("Successfully updated the journal entry", HttpStatus.OK);
                }
            }
            return new ResponseEntity<>("Journal Entry not found", HttpStatus.NOT_FOUND);


        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while updating journal entry: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

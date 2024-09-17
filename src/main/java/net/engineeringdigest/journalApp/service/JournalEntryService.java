package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional // [Atomicity] either all the steps will run or neither will run if one step is giving error
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userService.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            // save the journal entry in the database
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            // Add the saved journal entry to the user's list of journal entries
            user.getJournalEntries().add(saved);
            // Save the  user (with the new journal entry)
            userService.saveEntry(user);
        } catch (Exception e) {
            throw new RuntimeException("",e);
        }
    }
    // Overloaded method
    public void saveEntry(JournalEntry journalEntry) {
        // Save the journal entry in journal entry repository
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    public void deleteById(String username, ObjectId id) {
        User user = userService.findByUsername(username);
        // Remove the journal entry from the user's list
        user.getJournalEntries().removeIf(entry -> entry.getId().equals(id));
        // Save the user (with the updated journal entries)
        userService.saveEntry(user);
        journalEntryRepository.deleteById(id);
    }
}

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

            User user = userService.findByUsername(username);// Retrieve the user by username
            journalEntry.setDate(LocalDateTime.now()); // Set the current date for the journal entry
            JournalEntry saved = journalEntryRepository.save(journalEntry);// Save the journal entry in the database
            user.getJournalEntries().add(saved);// Add the saved journal entry to the user's list of journal entries
            userService.saveEntry(user);// Save the user (with the new journal entry)

        } catch (Exception e) {
            // Throw a runtime exception to ensure the transaction is rolled back
            throw new RuntimeException("Error saving journal entry: ", e);
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

package net.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.journalApp.entity.JournalEntry;
import net.journalApp.entity.User;
import net.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
            userService.saveUser(user);// Save the user (with the new journal entry)

        } catch (Exception e) {
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

    @Transactional
    public void deleteById(String username, ObjectId id) {
        try {
            User user = userService.findByUsername(username);
            // check if journal entry removed from user's journal entry list
            boolean removed =  user.getJournalEntries().removeIf(entry -> entry.getId().equals(id));
            if (removed) {
                // Save the user (with the updated journal entries)
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        } catch (Exception e) {
            log.error("Error in deleteById(Journal Entry Service): ", e);
            throw new RuntimeException("An error occurred in deleteById API: ",e);
        }
    }
}
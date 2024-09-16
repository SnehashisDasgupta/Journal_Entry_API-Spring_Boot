package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    public void saveEntry(JournalEntry journalEntry, String username) {
        User user = userService.findByUsername(username);
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepository.save(journalEntry);

        user.getJournalEntries().add(saved); // Add the saved journal entry to the user's list of journal entries
        userService.saveEntry(user); // Save the  user (with the new journal entry)
    }
    // Overloaded method
    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry); // Save the journal entry in journal entry repository
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    public void deleteById(String username, ObjectId id) {
        User user = userService.findByUsername(username);
        user.getJournalEntries().removeIf(entry -> entry.getId().equals(id)); // Remove the journal entry from the user's list
        userService.saveEntry(user); // Save the user (with the updated journal entries)
        journalEntryRepository.deleteById(id);
    }
}

package net.journalApp.scheduler;

import net.journalApp.cache.AppCache;
import net.journalApp.entity.JournalEntry;
import net.journalApp.entity.User;
import net.journalApp.repository.UserRepositoryImpl;
import net.journalApp.service.EmailService;
import net.journalApp.service.SentimentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private AppCache appCache;

    @Scheduled(cron = "0 0 9 * * SUN") //every Sunday at 9 AM email will be sent to the users
    public void fetchUsersAndSendSentimentAnalysisMail() {
        List<User> users = userRepository.getUserForSentimentAnalysis();
        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<String> filteredList = journalEntries.stream()
                    .filter(entry -> entry.getDate().isAfter(LocalDateTime.now().minusDays(7))) //Filters the stream to include only those journal entries whose date is after 7 days before the current date and time.
                    .map(JournalEntry::getContent)
                    .collect(Collectors.toList());
            String entry = String.join(" ", filteredList);
            String sentiment = sentimentAnalysisService.getSentiment(entry);
            emailService.sendEmail(user.getEmail(), "Sentiment for last 7 days", sentiment);
        }
    }

    @Scheduled(cron = "0 0/10 * ? * *")
    public void clearAppCache () {
        appCache.init(); //Clears the application cache every 10 min
    }
}

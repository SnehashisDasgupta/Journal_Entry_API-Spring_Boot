package net.journalApp.scheduler;

import net.journalApp.cache.AppCache;
import net.journalApp.entity.JournalEntry;
import net.journalApp.entity.User;
import net.journalApp.enums.Sentiment;
import net.journalApp.repository.UserRepositoryImpl;
import net.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

//    @Scheduled(cron = "0 0 9 * * SUN") //every Sunday at 9 AM email will be sent to the users
    public void fetchUsersAndSendSentimentAnalysisMail() {
        List<User> users = userRepository.getUserForSentimentAnalysis();
        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(entry -> entry.getDate().isAfter(LocalDateTime.now().minusDays(7))) //Filters the stream to include only those journal entries whose date is after 7 days before the current date and time.
                    .map(JournalEntry::getSentiment)
                    .collect(Collectors.toList());

            // send the sentiment which comes max in the week
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            // LOGIC
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null) {
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
                }
            }
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    mostFrequentSentiment = entry.getKey();
                    maxCount = entry.getValue();
                }
            }
            if (mostFrequentSentiment != null) {
                emailService.sendEmail(user.getEmail(), "Sentiment for last 7 days", mostFrequentSentiment.toString());
            }
        }
    }

    @Scheduled(cron = "0 0/10 * ? * *")
    public void clearAppCache () {
        appCache.init(); //Clears the application cache every 10 min
    }
}

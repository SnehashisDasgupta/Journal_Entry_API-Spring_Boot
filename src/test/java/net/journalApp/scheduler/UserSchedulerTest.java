package net.journalApp.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserSchedulerTest {

    @Autowired
    private UserScheduler userScheduler;

    @Test
    void testScheduleUserAndSendMail() {
        userScheduler.fetchUsersAndSendSentimentAnalysisMail();
    }
}

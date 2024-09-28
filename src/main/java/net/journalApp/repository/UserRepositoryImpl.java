package net.journalApp.repository;

import net.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepositoryImpl {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUserForSentimentAnalysis() {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")); // checks valid email through Regular expression
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true)); // where sentimentAnalysis is true
        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }
}

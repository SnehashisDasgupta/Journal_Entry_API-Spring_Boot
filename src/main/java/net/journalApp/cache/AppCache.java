package net.journalApp.cache;

import net.journalApp.entity.ConfigJournalAppEntity;
import net.journalApp.repository.ConfigJournalAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {
    public enum keys {
        WEATHER_API;
    }

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    // We will load the whole config collection in 'appCache' to reduce latency.[In-memory cache]
    public Map<String, String> appCache;

    @PostConstruct
    public void init() {
        appCache = new HashMap<>();
        List<ConfigJournalAppEntity> allKeyValuePairs = configJournalAppRepository.findAll();
        for (ConfigJournalAppEntity configJournalAppEntity : allKeyValuePairs) {
            appCache.put(configJournalAppEntity.getKey(), configJournalAppEntity.getValue()); // assign key and value to 'appCache' map
        }
    }
}

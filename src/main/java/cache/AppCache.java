package cache;

import entities.ConfigJournalAppEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import repositary.ConfigJournalAppRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    // Har baar mongo ke paas nhi jana hai hume ,
    // so jaise hi , iski bean banegi , hum
    // monogo ka data isme store kr lenge , taki hume bar bar mongo ko
    // call na karna pade

    public Map<String , String > APP_CACHE = new HashMap<>();

    // cache madhe store kela apan he journal entities.
    @PostConstruct
    public void init (){
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
        for (ConfigJournalAppEntity configJournalAppEntity : all){
            APP_CACHE.put("key", configJournalAppEntity.getKey());
            APP_CACHE.put("value", configJournalAppEntity.getValue());
        }
    }

}

package service;

import cache.AppCache;
import entities.weather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import repositary.ConfigJournalAppRepository;
import java.util.Objects;
import static jdk.javadoc.doclet.DocletEnvironment.ModuleMode.API;

@Slf4j
@Service
public class WeatherService {
//    private static final String apikey ;
//    private static final String API = "http://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

//    @Value("${weather.api.key}")
    @Autowired
    private RestTemplate restTemplate;

    //jaise hi maine appcache , ka bean banaya , uska , postconstruct run hua , aur , usne saara mongo data apne andar store kr liya
    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;

//    Yes ✅ you’re absolutely right — both code blocks are doing the same core logic:
//    Take API template from appCache
//    Replace placeholders (CITY, API_KEY)
//    Call restTemplate.exchange(...)
//    Return the weather response body

//    @GetMapping
//    public weather getWeather(String city) {
//        String finalAPI = appCache.APP_CACHE.get("value").replace("CITY", city).replace("API_KEY", appCache.APP_CACHE.get("key"));
//        ResponseEntity<weather> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, weather.class);
//        weather body = response.getBody();
//        System.out.println(body);
//        return body;

//        String apiTemplate = appCache.APP_CACHE.get("value");
//        String apiKey = appCache.APP_CACHE.get("key");
//
//        if (apiTemplate == null || apiKey == null) {
//            throw new RuntimeException("❌ Weather API config missing in AppCache!");
//        }
//
//        String finalAPI = apiTemplate
//                .replace("CITY", city)
//                .replace("API_KEY", apiKey);
//
//        ResponseEntity<weather> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, weather.class);
//        return response.getBody();
//    }localhost:8080/user
//
    public weather getWeather( String city){
//        We are saving the name of the city as key in the redis and the weather pojo class corresponding to that city
        weather weatherresponse = redisService.get("weather_of_" + city , weather.class);
        if(weatherresponse != null){
            return weatherresponse;
        }else {
            String apiTemplate = appCache.APP_CACHE.get("value");
            String apiKey = appCache.APP_CACHE.get("key");

             if (apiTemplate == null || apiKey == null) {
                throw new RuntimeException("❌ Weather API config missing in AppCache!");
             }

            String finalAPI = apiTemplate
                    .replace("CITY", city)
                    .replace("API_KEY", apiKey);
            ResponseEntity<weather> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, weather.class);
            weather body =  response.getBody();
            if( body != null){
                redisService.set("weather_of_" + city , body , 300l);
            }
        return body;
        }
    }
    @Autowired
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        log.info("WeatherService initialized with RestTemplate");
    }

}

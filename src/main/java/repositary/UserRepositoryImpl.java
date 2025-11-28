package repositary;

import entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class UserRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    // hume List of users chahiye jinka , Sentiment analysis active hai
    public List<User> getforSA(){
        Query query = new Query();
        //criteria add krna hai, , kya rules and regulations honge
//        query.addCriteria(Criteria.where("userName").is("JayMaharashtra3"));
        query.addCriteria(Criteria.where("email").regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$"));
        //user class madhe apan mention kela ahe , collection = "Users" ,, mg to direct user collection madhe jato
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
//        List<User> users  = mongoTemplate.find(query , User.class);
         List<User> users = mongoTemplate.find(query , User.class);
         return users;
    }
}

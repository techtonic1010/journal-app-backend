package repositary;

import entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

//@NoRepositoryBean
//@Component

public interface UserRepository extends MongoRepository <User, ObjectId> {
    User findByUserName(String userName);
    void deleteByUserName(String username);
}

package service;

import entities.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import repositary.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    //implementation ka object runtime pe
    // serivce mei daal dega
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User savenewadmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
        return userRepository.save(user);
    }

    public  User savenewUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        // Email is already set from the DTO/User object, no need to override
        return userRepository.save(user);
    }

    public  User saveEntry(User user){
        return userRepository.save(user);
    }

    public List<User> getAllEntries() {
        return userRepository.findAll();
    }

    public Optional< User> findbyId(ObjectId id){
        return userRepository.findById( id);
    }
    public void deleteById(ObjectId id) {
        userRepository.deleteById( id);
    }

    public Optional<User> findById(ObjectId id) {
        return userRepository.findById(id);
    }
    public boolean existsById(ObjectId id) {
        return userRepository.existsById(id);
    }


    // didnt understand this method
    public  User findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }


}

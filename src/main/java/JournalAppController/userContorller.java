package JournalAppController;
import dto.UserUpdateDTO;
import entities.User;
import entities.weather;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import repositary.UserRepository;
import service.EmailService;
import service.UserService;
import service.WeatherService;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "User APIs" , description = "Read , update , delete")
public class userContorller {

//    private final Map<Long, JournalEntry> journalEntries = new HashMap<>();
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private EmailService emailService;
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("✅ user controller api is working.");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInDb = userService.findByUserName(username);
        
        // Only update fields that are provided
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            userInDb.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            userInDb.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        userService.savenewUser(userInDb);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserByUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         yaha tak execution tabhi ayega , jab user authenticate hoga
        String username = authentication.getName();
        userRepository.deleteByUserName(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    public  userContorller() {
        log.info("UserController loaded");
    }

    @GetMapping("/greet")
    public ResponseEntity<?> greeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        weather weatherResponse = weatherService.getWeather("Mumbai");
        String greeting = "";
        if(weatherResponse != null){
            int feelslike = weatherResponse.getCurrent().getFeelslike();
            greeting = " | Weather feels like " + feelslike;
        }
        return new ResponseEntity<>("Hi " + authentication.getName() + greeting , HttpStatus.OK);
    }

    @GetMapping("/sendMail")
    public void sendmail(){
        emailService.sendmail(
                "subtlegrowth718@gmail.com",
                "Testing Java Mail Sender",
                "Hi , I kya mei aapke DM mei aa sakta hu "
        );
    }

}

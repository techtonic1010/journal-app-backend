package JournalAppController;


import entities.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repositary.UserRepository;
import service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs" )
public class adminController {
    @Autowired
    private UserService userService;

//    @Autowired
//    private UserService userService;

    @GetMapping
    private ResponseEntity<?> getAllEntries(){
        List<User> all = userService.getAllEntries();
        if(all != null && !all.isEmpty()){
            return new ResponseEntity<>(all , HttpStatus.OK);
        }
        return new ResponseEntity<>(all , HttpStatus.NOT_FOUND);
    }

    @PostMapping
    private ResponseEntity<?> createnewAdmin(@RequestBody User user){
        return new ResponseEntity<> (userService.savenewadmin(user) , HttpStatus.OK);
    }
}

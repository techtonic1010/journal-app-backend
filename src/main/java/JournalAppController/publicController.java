package JournalAppController;

import dto.LoginDTO;
import dto.UserSignupDTO;
import entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import service.UserDetailService;
import service.UserService;
import utils.JwtUtil;

@Slf4j
@RestController
@RequestMapping("/public")
@Tag(name = "Public APIs", description = "Public endpoints for user registration, login, and health check (no authentication required)")
public class publicController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Operation(
            summary = "Register a new user",
            description = "Create a new user account with username, email, and password. Passwords are encrypted using BCrypt before storage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserSignupDTO dto) {
        // Convert DTO to Entity
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        
        User savedUser = userService.savenewUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Operation(
            summary = "Health check",
            description = "Simple endpoint to check if the API is running and accessible"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API is healthy",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/health-check")
    public String healthCheck() {
        return "Ok";
    }
    
    @Operation(
            summary = "User login",
            description = "Authenticate user with username and password. Returns a JWT token valid for 50 minutes. Use this token in the 'Authorize' button above."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful - JWT token returned",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO dto){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUserName(), dto.getPassword())
        );
        UserDetails userDetails = userDetailService.loadUserByUsername(dto.getUserName());
        String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return new ResponseEntity<>(jwt , HttpStatus.OK);
    }
    
    @Operation(
            summary = "Test Redis connection",
            description = "Test endpoint to verify Redis connectivity. Sets and retrieves a test key-value pair."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Redis test successful",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Redis connection failed",
                    content = @Content)
    })
    @GetMapping("/test-redis")
    public String testRedis() {
        try {
            redisTemplate.opsForValue().set("publicTestKey", "helloFromPublicRedis");
            String value = redisTemplate.opsForValue().get("publicTestKey");
            return "Public Redis Test Success! Value = " + value;
        } catch (Exception e) {
            return "Public Redis Test Failed: " + e.getMessage();
        }
    }
}
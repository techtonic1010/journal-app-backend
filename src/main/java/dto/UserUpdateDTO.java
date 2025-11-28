package dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    private String password;
}

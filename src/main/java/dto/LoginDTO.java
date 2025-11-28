package dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request")
public class LoginDTO {
    
    @Schema(description = "Username", example = "john_doe", required = true)
    @NotBlank(message = "Username is required")
    private String userName;
    
    @Schema(description = "Password", example = "SecurePass123!", required = true)
    @NotBlank(message = "Password is required")
    private String password;
}

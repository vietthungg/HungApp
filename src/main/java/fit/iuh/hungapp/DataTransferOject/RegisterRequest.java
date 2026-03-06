package fit.iuh.hungapp.DataTransferOject;

import lombok.Data;

@Data // Tự động tạo Getter/Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
}
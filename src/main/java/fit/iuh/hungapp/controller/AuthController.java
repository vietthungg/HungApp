package fit.iuh.hungapp.controller;

import fit.iuh.hungapp.DataTransferOject.RegisterRequest;
import fit.iuh.hungapp.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Tiền tố chung cho các API xác thực
public class AuthController {

    @Autowired
    private AuthService authService;

    // API Đăng ký tài khoản mới
    @PostMapping("/create")
    public String createAccount(@RequestBody RegisterRequest request) {
        return authService.createUser(request);
    }
}
package fit.iuh.hungapp.Service;

import fit.iuh.hungapp.DataTransferOject.RegisterRequest;
import fit.iuh.hungapp.entity.User;
import fit.iuh.hungapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Đánh dấu đây là một lớp xử lý nghiệp vụ
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public String createUser(RegisterRequest request) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Thất bại: Tên đăng nhập đã tồn tại!";
        }

        // 2. Chuyển dữ liệu từ DTO sang Entity để lưu
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(request.getPassword()); // Tạm thời lưu thẳng, lát nữa ráp Security sẽ mã hóa sau
        newUser.setEmail(request.getEmail());

        // 3. Gọi Repository để lưu xuống SQL Server
        userRepository.save(newUser);

        return "Thành công: Đã tạo tài khoản cho " + request.getUsername();
    }
}
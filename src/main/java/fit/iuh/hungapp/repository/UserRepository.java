package fit.iuh.hungapp.repository;

import fit.iuh.hungapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Spring Boot ảo diệu ở chỗ: Bạn chỉ cần đặt tên hàm đúng quy tắc,
    // nó sẽ tự dịch ra câu lệnh SQL tìm User theo username cho bạn!
    Optional<User> findByUsername(String username);

    // Kiểm tra xem username hoặc email đã tồn tại chưa (dùng lúc Đăng ký)
    boolean existsByUsername(String username);
}
package fit.iuh.hungapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data // Annotation của Lombok giúp tự động tạo Getter/Setter
@Entity // Báo cho Spring biết đây là 1 thực thể database
@Table(name = "Users") // Tên bảng trong SQL Server
public class User {

    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng (IDENTITY trong SQL)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String email;
}
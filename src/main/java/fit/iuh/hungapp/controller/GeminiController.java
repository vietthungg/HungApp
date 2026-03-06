package fit.iuh.hungapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fit.iuh.hungapp.Service.GeminiService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/ask")
    public ResponseEntity<String> askAI(@RequestBody PromptRequest request) {
        try {
            log.info("Received prompt : {}", request.prompt());
            
            String answer = geminiService.askAI(request.prompt());
            
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            log.error("Lỗi khi gọi tới Gemini API: ", e);
            return ResponseEntity.internalServerError()
                    .body("Hệ thống chuyên gia đang bận, vui lòng thử lại sau: " + e.getMessage());
        }
    }

    // Sử dụng Record để tạo DTO nhận data (hỗ trợ từ Java 14+)
    public record PromptRequest(String prompt) {}
}
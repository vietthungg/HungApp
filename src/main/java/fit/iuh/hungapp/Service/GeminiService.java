package fit.iuh.hungapp.Service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${api.key}")
    private String apiKey;

    private Client client;
    private static final String MODEL_NAME = "gemini-2.5-flash"; // Hoặc gemini-2.5-flash

    @PostConstruct
    public void init() {
        // Khởi tạo Client siêu đơn giản với API Key (không lo lỗi Credentials nữa)
        this.client = Client.builder().apiKey(apiKey).build();
    }

    public String askAI(String userInput) {
        String systemInstruction = "Vai trò: Bạn là một chuyên gia tư vấn kinh doanh F&B với hơn 15 năm kinh nghiệm quản lý các chuỗi cửa hàng đặc sản tại Việt Nam, đặc biệt am hiểu về mô hình quán Bánh Canh...";

        try {
            // Cấu hình Prompt hệ thống
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .temperature(0.2f)
                    .systemInstruction(Content.fromParts(Part.fromText(systemInstruction)))
                    .build();

            // Gọi AI
            GenerateContentResponse response = client.models.generateContent(
                    MODEL_NAME,
                    userInput,
                    config
            );

            return response.text();

        } catch (Exception e) {
            logger.error("Lỗi khi gọi Gemini API: ", e);
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }
}
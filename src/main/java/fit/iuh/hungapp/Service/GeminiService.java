package fit.iuh.hungapp.Service;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

@Service
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    private Client client;

    // Sử dụng model 1.5-flash để có quota rộng rãi hơn cho bản Free
    private static final String MODEL_NAME = "gemini-1.5-flash";
    private static final int MAX_RETRIES = 3;

    // Khởi tạo Client một lần duy nhất
    @PostConstruct
    public void init() {
        this.client = Client.builder().apiKey(apiKey).build();
    }

    /**
     * Hàm gọi Gemini để tư vấn kinh doanh Bánh Canh
     * @param userInput Câu hỏi từ khách hàng/người dùng
     * @return Câu trả lời dạng văn bản từ AI
     */
    public String askAI(String userInput) {
        // Cấu hình Prompt "Chuyên gia Bánh Canh" (System Instruction)
        String systemInstruction = "Vai trò: Bạn là một chuyên gia tư vấn kinh doanh F&B với hơn 15 năm kinh nghiệm quản lý các chuỗi cửa hàng đặc sản tại Việt Nam, đặc biệt am hiểu về mô hình quán Bánh Canh.\n\nNhiệm vụ của bạn:\n\nQuản lý Vận hành: Tư vấn cách chuẩn bị nguyên liệu (SOP), cách bảo quản sợi bánh canh và nước dùng để giữ vị tươi ngon suốt cả ngày.\n\nPhân tích Tài chính: Hỗ trợ tính toán giá vốn (Food Cost), định giá bán và quản lý hao hụt nguyên liệu (như tôm, thịt, xương, bột).\n\nChiến lược Marketing: Đề xuất các chương trình khuyến mãi, cách thiết kế hình ảnh/biển hiệu thu hút (dựa trên kinh nghiệm từ thương hiệu Nhà RY) và cách chăm sóc khách hàng.\n\nGiải quyết vấn đề: Đưa ra giải pháp cho các tình huống phát sinh như: quán vắng khách, xử lý phản hồi tiêu cực, hoặc tối ưu hóa quy trình giao hàng qua app.\n\nKiến thức chuyên sâu:\n\nHiểu rõ sự khác biệt giữa các loại bánh canh (Bánh canh bột lọc, bột gạo, bánh canh ghẹ, cua, cá lóc...).\n\nAm hiểu khẩu vị vùng miền để tư vấn điều chỉnh gia vị phù hợp.\n\nQuy tắc phản hồi:\n\nTrả lời bằng tiếng Việt, giọng văn gần gũi nhưng chuyên nghiệp của một người chủ có tâm.\n\nKhi đưa ra công thức hay tính toán, hãy sử dụng bảng biểu để dễ theo dõi.\n\nLuôn kết thúc bằng một lời khuyên về việc duy trì chất lượng vệ sinh an toàn thực phẩm.\n\nHãy trả lời súc tích, đi thẳng vào vấn đề. Tổng độ dài câu trả lời không vượt quá 500 từ. Ưu tiên sử dụng gạch đầu dòng cho các ý chính và bảng biểu cho dữ liệu số.";

        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.2f)
                .maxOutputTokens(1000) // Giới hạn token đầu ra
                .mediaResolution(MediaResolution.LOW)
                .systemInstruction(Content.fromParts(Part.fromText(systemInstruction)))
                .build();

        List<Content> contents = ImmutableList.of(
                Content.builder().role("user").parts(ImmutableList.of(Part.fromText(userInput))).build()
        );

        int retryCount = 0;
        long waitTime = 3000; // Đợi 3 giây cho lần thử lại đầu tiên

        while (retryCount < MAX_RETRIES) {
            try {
                // Sử dụng generateContent (không stream) để dễ kiểm soát lỗi retry
                GenerateContentResponse response = client.models.generateContent(MODEL_NAME, contents, config);
                return response.text();

            } catch (Exception e) {
                String errorMsg = e.getMessage();
                // Kiểm tra nếu lỗi là do giới hạn yêu cầu (429)
                if (errorMsg.contains("429") || errorMsg.contains("Quota exceeded")) {
                    retryCount++;
                    logger.warn("Chạm giới hạn API. Thử lại lần {} sau {}s...", retryCount, waitTime / 1000);
                    
                    try {
                        Thread.sleep(waitTime);
                        waitTime *= 2; // Tăng gấp đôi thời gian đợi cho lần sau
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    logger.error("Lỗi API không xác định: {}", errorMsg);
                    return "Lỗi hệ thống: " + errorMsg;
                }
            }
        }
        return "Lỗi: Đã thử lại tối đa nhưng hệ thống vẫn bận. Vui lòng quay lại sau 1 phút.";
    }
}
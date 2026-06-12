package se.rasha.ai_lab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final AiClientService aiClientService;

    public TestController(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

    @GetMapping("/run-test")
    public String runTest() {
        String result = aiClientService.analyzeText("I love Java programming");
        AiResponseDto dto = aiClientService.parseResponse(result);

        return dto.toString();
    }
}
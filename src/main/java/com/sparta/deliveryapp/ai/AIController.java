package com.sparta.deliveryapp.ai;


import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AIController {
  private final GeminiService geminiService;

  @PostMapping("/ask")
  public Mono<AIResponseDto> askAI(@RequestBody Map<String, String> request) {
    String prompt = request.get("prompt");
    return geminiService.askGemini(prompt);
  }
}

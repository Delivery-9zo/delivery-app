package com.sparta.deliveryapp.ai;


import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI API", description = "Gemini Api를 이용한 서비스 입니다.")
public class AIController {
  private final GeminiService geminiService;

  @PostMapping("/ask")
  public Mono<AIResponseDto> askAI(@RequestBody AIRequestDto requestDto, @AuthenticationPrincipal
      UserDetailsImpl userDetails) {
    String prompt = requestDto.getPrompt();
    return geminiService.askGemini(prompt,userDetails.getUser());
  }
}

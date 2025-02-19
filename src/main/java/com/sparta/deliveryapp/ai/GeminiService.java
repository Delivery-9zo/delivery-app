package com.sparta.deliveryapp.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class GeminiService {

  private final WebClient webClient;
  private final AIRepository aiRepository;


  @Value("${spring.gemini.secretKey}")
  private String apiKey;


  public GeminiService(WebClient.Builder webClientBuilder,AIRepository aiRepository) {
    this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
    this.aiRepository = aiRepository;
  }

  public Mono<AIResponseDto> askGemini(String prompt) {

    Map<String, Object> requestBody = Map.of(
        "contents", new Object[] {
            Map.of("parts", new Object[] {
                Map.of("text", prompt+"50자 이내로 답변해주면서 한줄로 작성해줘")
            })
        }
    );

    return webClient.post()
        .uri("/v1/models/gemini-1.5-flash:generateContent?key=" + apiKey)
        .header("Content-Type", "application/json")
        .bodyValue(requestBody)
        .retrieve()
        .bodyToMono(String.class)
        .publishOn(Schedulers.boundedElastic())
        .map(reponse -> {
          AI ai = new AI();
          ai.setPrompt(prompt);
          ai.setAnswer(extractTextFromResponse(reponse));
          ai.setCreatedAt(LocalDateTime.now());
          aiRepository.save(ai);

          return new AIResponseDto(ai);
        });

  }

  // 응답에서 "text" 부분만 추출하는 메서드
  private String extractTextFromResponse(String response) {
    // 여기에 응답을 JSON으로 파싱하고 text 부분을 추출하는 로직을 추가

    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
    JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
    JsonObject content = firstCandidate.getAsJsonObject("content");
    JsonArray parts = content.getAsJsonArray("parts");
    String text = parts.get(0).getAsJsonObject().get("text").getAsString();

    return text;
  }

}

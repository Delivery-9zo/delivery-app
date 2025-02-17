package com.sparta.deliveryapp.store.util.kakaoLocal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

@Service
public class KakaoLocalAPI {

  private final WebClient webClient;
  private final ObjectMapper objectMapper;

  @Value("${api-key.key}")
  private String kakaoLocalApiUrl;

  @Value("${api-key.key}")
  private String kakaoLocalApiKey;

  public KakaoLocalAPI(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
    this.webClient = webClientBuilder.baseUrl(kakaoLocalApiUrl).build();
    this.objectMapper = objectMapper;
  }

  /**
   * 주소를 입력받아 해당 주소의 좌표(위도, 경도)를 반환하는 메서드
   *
   * @param address 주소
   * @return {latitude, longitude} 좌표
   */
  public double[] getCoordsFromAddress(String address) {
    String responseJson = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .queryParam("query", address)
            .build())
        .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoLocalApiKey)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    try {
      JsonNode root = objectMapper.readTree(responseJson);
      JsonNode documents = root.path("documents");

      if (documents.isEmpty()) {
        throw new IllegalArgumentException("유효하지 않은 주소입니다.");
      }

      JsonNode firstResult = documents.get(0);
      double longitude = firstResult.path("x").asDouble(); // 경도
      double latitude = firstResult.path("y").asDouble(); // 위도

      return new double[]{longitude, latitude};

    } catch (Exception e) {
      throw new RuntimeException("주소 변환 중 오류 발생", e);
    }
  }

}

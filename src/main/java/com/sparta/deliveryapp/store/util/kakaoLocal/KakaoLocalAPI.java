package com.sparta.deliveryapp.store.util.kakaoLocal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class KakaoLocalAPI {

  private final WebClient webClient;
  private final ObjectMapper objectMapper;

  @Value("${api-key.url}")
  private String kakaoApiUrl;

  @Value("${api-key.key}")
  private String kakaoLocalApiKey;

  public KakaoLocalAPI(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
    this.webClient = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE,
        MediaType.APPLICATION_JSON_VALUE).build();
    this.objectMapper = objectMapper;
  }

  /**
   * 주어진 주소를 카카오 로컬 API를 통해 검색하고, 해당 주소의 좌표(위도, 경도)를 반환
   *
   * @param address 주소
   * @return {latitude, longitude} 좌표
   */
  public double[] getCoordsFromAddress(String address) {

    // 주소가 비어있는지 체크
    if (address == null || address.isEmpty()) {
      throw new IllegalArgumentException("주소가 없습니다.");
    }

    // 주소 인코딩
//    String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);

    String uri = UriComponentsBuilder.fromUriString(kakaoApiUrl + "/v2/local/search/address.json")
        .queryParam("analyze_type", "similar")
        .queryParam("page", 1)
        .queryParam("size", 5)
        .queryParam("query", address)
        .build()
        .toUriString();
    log.info(uri);
    try {
      String responseJson = webClient.get()
          .uri(uri)
          .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoLocalApiKey)
          .header("Content-Type", "application/json; charset=UTF-8")
          .retrieve()
          .bodyToMono(String.class)
          .block();

      // JSON 응답 파싱
      JsonNode root = objectMapper.readTree(responseJson);
      log.info(root.toString());
      JsonNode documents = root.path("documents");

      // 검색 결과 없을 때
      if (documents.isEmpty()) {
        throw new IllegalArgumentException("유효하지 않은 주소이거나, 검색 결과가 없습니다.");
      }

      // 첫 번째 검색 결과에서 경도와 위도 추출
      JsonNode firstResult = documents.get(0);

      // x, y 정보 가져오기 (주소 객체에서)
      double longitude = 0.0;
      double latitude = 0.0;

      // address 객체 안에서 x, y 찾기
      JsonNode resultAddress = firstResult.path("address");
      if (resultAddress != null && !resultAddress.isMissingNode()) {
        longitude = resultAddress.path("x").asDouble();
        latitude = resultAddress.path("y").asDouble();
      } else {
        // road_address 객체 안에서 x, y 찾기
        JsonNode roadAddress = firstResult.path("road_address");
        if (roadAddress != null && !roadAddress.isMissingNode()) {
          longitude = roadAddress.path("x").asDouble();
          latitude = roadAddress.path("y").asDouble();
        } else {
          throw new IllegalArgumentException("좌표 정보가 없습니다.");
        }
      }

      // 좌표 반환
      return new double[]{longitude, latitude};

    } catch (WebClientResponseException e) {
      log.error("WebClientResponseException 발생: Status={}, Body={}", e.getStatusCode(),
          e.getResponseBodyAsString(), e);
      if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        throw new RuntimeException("카카오 API 접근이 거부되었습니다. API 키를 확인하세요.");
      } else {
        throw new RuntimeException("WebClient 오류 발생: " + e.getMessage(), e);
      }
    } catch (Exception e) {
      log.error("주소 변환 중 오류 발생" + e.getMessage(), e);
      throw new RuntimeException("주소 변환 중 오류 발생", e);
    }
  }
}
package com.sparta.deliveryapp.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AIResponseDto {

  private String response;  // AI의 응답 내용

  public AIResponseDto(AI ai) {
    this.response = ai.getAnswer();
  }
}

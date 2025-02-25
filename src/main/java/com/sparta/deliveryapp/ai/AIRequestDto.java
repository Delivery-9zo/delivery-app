package com.sparta.deliveryapp.ai;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "AI 요청에 필요한 데이터")
public class AIRequestDto {

  @Schema(description = "AI에 전달할 프롬프트", example = "맛있는 고깃집 이름 추천해줘")
  private String prompt;
}

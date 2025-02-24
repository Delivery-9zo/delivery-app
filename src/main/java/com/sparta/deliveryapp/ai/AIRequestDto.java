package com.sparta.deliveryapp.ai;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "AI 요청에 필요한 데이터")
public class AIRequestDto {

  @Schema(description = "AI에 전달할 프롬프트", example = "string")
  private String prompt;
}

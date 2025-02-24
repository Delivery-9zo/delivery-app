package com.sparta.deliveryapp.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@OpenAPIDefinition(
    info = @Info(title = "배달 및 포장 음식 주문 관리 플랫폼",
        description = "Delivery 9zo(rescue)의 배달 및 포장 주문 관리, 결제, 주문 내역 관리 및 사용자 서비스",
        version = "v1",
        contact = @Contact(name = "delivery-9zo", url = "www.sparta-delivery-9zo.p-e.kr", email = "")
    ))
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI(){
    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER).name("Authorization");
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

    return new OpenAPI()
        .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
        .security(Arrays.asList(securityRequirement));
  }

}

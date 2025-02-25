package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreUpdateRequestDto;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/master/stores")
@PreAuthorize("hasAuthority('ROLE_MASTER')")
@Tag(name = "상점 기능 마스터유저 API", description = "마스터 권한을 가진 사람이 사용 가능한 API")
public class StoreMasterController {

  private final StoreService storeService;

  @Operation(
      summary = "가게 삭제 기능",
      description = "가게 UUID를 이용하여 가게를 삭제하는 API. 요청한 유저와 가게 주인 정보가 일치해야 삭제 가능.",
      parameters = {
          @Parameter(
              name = "storeId",
              description = "삭제할 가게의 UUID",
              required = true,
              example = "f6419401-ac0e-4138-88a3-a53933b25e17"
          )
      }
  )
  @ApiResponse(
      responseCode = "200",
      description = "가게 삭제 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          value = "{\n" +
              "  \"message\": \"가게가 정상적으로 삭제되었습니다.\"\n" +
              "}"
      ))
  )
  @DeleteMapping("")
  public ResponseEntity<String> deleteStore(
      @RequestParam(name = "storeId") String storeId,
  @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.deleteStore(storeId, userDetails);

    return ResponseEntity.ok().body("가게가 정상적으로 삭제되었습니다.");
  }

  //이름으로 가게 목록 조회
  @Operation(
      summary = "가게 이름으로 검색",
      description = "가게 이름을 검색하여 일치하는 이름의 가게 목록을 조회하는 API. 기본 정렬 순서는 생성 시간 오름차순.",
      parameters = {
          @Parameter(
              name = "storeName",
              description = "검색할 가게 이름",
              required = true,
              example = "양식중국집"
          ),
          @Parameter(
              name = "size",
              description = "페이지 크기 (기본값: 10)",
              example = "10"
          ),
          @Parameter(
              name = "page",
              description = "페이지 번호 (0부터 시작)",
              example = "0"
          ),
          @Parameter(
              name = "sort",
              description = "정렬 기준 필드 (예: createdAt,asc 또는 createdAt,desc)",
              example = "createdAt,asc"
          )
      }
  )
  @ApiResponse(
      responseCode = "200",
      description = "가게 이름 검색 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "가게 이름 검색 성공 예시",
          value = "{\n" +
              "  \"content\": [\n" +
              "    {\n" +
              "      \"store_id\": \"storeUuid\",\n" +
              "      \"store_name\": \"양식중국집\",\n" +
              "      \"address\": \"서울 종로구 종로5길 58 광해관리공단 1층\",\n" +
              "      \"category\": [\"중식\", \"양식\"]\n" +
              "    }\n" +
              "  ],\n" +
              "  \"pageable\": {\n" +
              "    \"pageNumber\": 0,\n" +
              "    \"pageSize\": 10\n" +
              "  },\n" +
              "  \"totalElements\": 1,\n" +
              "  \"totalPages\": 1\n" +
              "}"
      ))
  )
  @GetMapping("")
  public ResponseEntity<Page<StoreResponseDto>> findStoresByStoreName(
      @RequestParam(value = "storeName") String storeName,
      @PageableDefault(
          size = 10,
          page = 0,
          sort = {"createdAt", "updatedAt"},
          direction = Direction.ASC) Pageable pageable) {

    Page<StoreResponseDto> storeResponseDtos = storeService.findStoresByStoreName(storeName,
        pageable);

    return ResponseEntity.ok().body(storeResponseDtos);
  }

  //가게 id로 가게 조회
  @Operation(
      summary = "가게 UUID로 조회",
      description = "가게 UUID로 조회하여 가게 정보를 제공하는 API. 가게 주인과 서버 관리자만 이용 가능.",
      parameters = {
          @Parameter(
              name = "sid",
              description = "조회할 가게의 UUID",
              required = true,
              example = "7706e22d-ea15-41e2-aa3f-c1db493ae74a"
          )
      }
  )
  @ApiResponse(
      responseCode = "200",
      description = "가게 UUID 조회 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "가게 UUID 조회 성공 예시",
          value = "{\n" +
              "  \"store_id\": \"7706e22d-ea15-41e2-aa3f-c1db493ae74a\",\n" +
              "  \"store_name\": \"양식중국집\",\n" +
              "  \"address\": \"서울 종로구 종로5길 58 광해관리공단 1층\",\n" +
              "  \"b_regi_num\": \"456-78-90123\",\n" +
              "  \"open_at\": \"12:00\",\n" +
              "  \"close_at\": \"23:00\",\n" +
              "  \"category\": [\"중식\", \"양식\"]\n" +
              "}"
      ))
  )
  @GetMapping("/sid")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  public ResponseEntity<StoreResponseDto> findStoresByStoreId(
      @RequestParam(name = "sid") String storeId) {

    StoreResponseDto storeResponseDto = storeService.findStoresByStoreId(UUID.fromString(storeId));

    return ResponseEntity.ok().body(storeResponseDto);
  }

  @Operation(
      summary = "가게 정보 수정(master 기능)",
      description = "가게 정보를 수정하는 API. 기존 정보와 변한 정보만 수정되어 저장. 요청한 유저와 가게 주인 정보가 일치해야 수정 가능.",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "수정할 가게 정보",
          required = true,
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "가게 정보 수정 요청 예시",
                  value = "{\n" +
                      "  \"store_id\": \"storeUuid\",\n" +
                      "  \"store_name\": \"양식중국집 종로구\",\n" +
                      "  \"address\": \"서울 종로구 종로5길 58 광해관리공단 1층\",\n" +
                      "  \"b_regi_num\": \"456-78-90123\",\n" +
                      "  \"open_at\": \"12:00\",\n" +
                      "  \"close_at\": \"23:00\",\n" +
                      "  \"category\": [\"중식\", \"양식\", \"한식\"]\n" +
                      "}"
              )
          )
      )
  )
  @ApiResponse(
      responseCode = "200",
      description = "가게 정보 수정 성공",
      content = @Content(
          mediaType = "application/json",
          examples = @ExampleObject(
              value = "{\n" +
                  "  \"message\": \"가게 정보가 정상적으로 수정되었습니다.\"\n" +
                  "}"
          )
      )
  )
  @PutMapping("")
  @PreAuthorize("hasAnyAuthority('ROLE_MASTER')")
  public ResponseEntity<String> updateStore(
      @RequestBody StoreUpdateRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.updateStoreMaster(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게 정보가 정상적으로 수정되었습니다.");
  }

}

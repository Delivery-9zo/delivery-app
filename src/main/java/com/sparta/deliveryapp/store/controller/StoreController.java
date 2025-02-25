package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.order.dto.SearchOrderResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreResponseDto;
import com.sparta.deliveryapp.store.dto.StoreNearbyStoreWithCategoryResponseDto;
import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.dto.StoreUpdateRequestDto;
import com.sparta.deliveryapp.store.service.StoreService;
import com.sparta.deliveryapp.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
@Tag(name = "상점 기능 API", description = "상점 등록, 검색, 정보 수정이 가능한 API")
public class StoreController {

  private final StoreService storeService;

  @ApiResponse(responseCode = "200", description = "가게 등록 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "가게 등록 성공 메시지",
          value = "가게가 성공적으로 등록되었습니다."
      )))
  @Operation(
      summary = "가게 등록 기능",
      description = "가게를 등록하는 API",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "가게 등록 요청 데이터",
          required = true,
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  name = "가게 등록 요청 예시",
                  value = "{\n" +
                      "  \"store_name\": \"양식중국집 종로구\",\n" +
                      "  \"address\": \"서울 종로구 종로5길 58 광해관리공단 1층\",\n" +
                      "  \"b_regi_num\": \"456-78-90123\",\n" +
                      "  \"open_at\": \"12:00\",\n" +
                      "  \"close_at\": \"23:00\",\n" +
                      "  \"category\": [\"중식\", \"양식\"]\n" +
                      "}"
              )
          )
      )
  )
  @Parameter(name = "size", description = "페이지 크기", example = "10")
  @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
  @Parameter(name = "sort", description = "정렬 기준 필드 (예: createdAt,asc 또는 createdAt,desc)",
      example = "createdAt,desc")
  @PostMapping("/regi")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  public ResponseEntity<String> regiStore(
      @Valid @RequestBody StoreRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.regiStore(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게가 정상적으로 등록되었습니다.");
  }

  //카테고리 없이 경위도로만 근처 가게 검색

  @Operation(
      summary = "인근 가게 정보 검색(카테고리 미지정 시)",
      description = "인근 3km 이내의 모든 가게를 조회하는 API",
      parameters = {
          @Parameter(
              name = "longitude",
              description = "사용자의 경도 (예: 126.996337085397)",
              required = true,
              example = "126.996337085397"
          ),
          @Parameter(
              name = "latitude",
              description = "사용자의 위도 (예: 37.5809095481038)",
              required = true,
              example = "37.5809095481038"
          ),
          @Parameter(
              name = "size",
              description = "페이지 크기 (기본값: 10)",
              example = "5"
          ),
          @Parameter(
              name = "page",
              description = "페이지 번호 (0부터 시작)",
              example = "0"
          ),
          @Parameter(
              name = "sort",
              description = "정렬 기준 필드 (예: createdAt,asc 또는 updatedAt,desc)",
              example = "createdAt,asc"
          )
      }
  )
  @ApiResponse(
      responseCode = "200",
      description = "가게 정보 조회 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "가게 정보 예시",
          value = "{\n" +
              "  \"content\": [\n" +
              "    {\n" +
              "      \"store_name\": \"양식중국집 종로구2\",\n" +
              "      \"address\": \"서울 종로구 종로5길 58\",\n" +
              "      \"distanceFromRequest\": 2.5\n" +
              "    },\n" +
              "    {\n" +
              "      \"store_name\": \"한식당 종로구1\",\n" +
              "      \"address\": \"서울 종로구 종로3길 12\",\n" +
              "      \"distanceFromRequest\": 1.8\n" +
              "    }\n" +
              "  ],\n" +
              "  \"pageable\": \"INSTANCE\",\n" +
              "  \"totalPages\": 1,\n" +
              "  \"totalElements\": 2,\n" +
              "  \"last\": true,\n" +
              "  \"size\": 5,\n" +
              "  \"number\": 0,\n" +
              "  \"sort\": {\n" +
              "    \"empty\": false,\n" +
              "    \"sorted\": true,\n" +
              "    \"unsorted\": false\n" +
              "  },\n" +
              "  \"first\": true,\n" +
              "  \"numberOfElements\": 2,\n" +
              "  \"empty\": false\n" +
              "}"
      ))
  )
  @GetMapping("/location")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<StoreNearbyStoreResponseDto>> getNearbyStoresWithoutCategory(
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @PageableDefault(size = 10, page = 0,
          sort = {"createdAt", "updatedAt", "distanceFromRequest"},
          direction = Direction.ASC) Pageable pageable) {

    Page<StoreNearbyStoreResponseDto> storeResponseDto = storeService.findNearbyStoresWithoutCategory(
        longitude, latitude, pageable);

    return ResponseEntity.ok().body(storeResponseDto);
  }

  //카테고리에 해당하는 주변 가게 검색
  @Operation(
      summary = "인근 가게 정보 검색(카테고리 지정 시)",
      description = "인근 3km 이내의 일치하는 카테고리의 모든 가게를 조회하는 API",
      parameters = {
          @Parameter(
              name = "category",
              description = "검색할 카테고리 목록 (예: 중식, 양식)",
              required = true,
              example = "중식"
          ),
          @Parameter(
              name = "longitude",
              description = "사용자의 경도 (예: 126.996337085397)",
              required = true,
              example = "126.996337085397"
          ),
          @Parameter(
              name = "latitude",
              description = "사용자의 위도 (예: 37.5809095481038)",
              required = true,
              example = "37.5809095481038"
          ),
          @Parameter(
              name = "size",
              description = "페이지 크기 (기본값: 10)",
              example = "5"
          ),
          @Parameter(
              name = "page",
              description = "페이지 번호 (0부터 시작)",
              example = "0"
          ),
          @Parameter(
              name = "sort",
              description = "정렬 기준 필드 (예: createdAt,asc 또는 distanceFromRequest,asc)",
              example = "createdAt,asc"
          )
      }
  )
  @ApiResponse(
      responseCode = "200",
      description = "카테고리에 해당하는 인근 가게 정보 조회 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "가게 정보 예시",
          value = "{\n" +
              "  \"content\": [\n" +
              "    {\n" +
              "      \"store_name\": \"양식중국집 종로구2\",\n" +
              "      \"address\": \"서울 종로구 종로5길 58\",\n" +
              "      \"distanceFromRequest\": 2.5,\n" +
              "      \"category\": [\"중식\", \"양식\"]\n" +
              "    },\n" +
              "    {\n" +
              "      \"store_name\": \"양식당 종로구3\",\n" +
              "      \"address\": \"서울 종로구 종로3길 12\",\n" +
              "      \"distanceFromRequest\": 1.8,\n" +
              "      \"category\": [\"양식\"]\n" +
              "    }\n" +
              "  ],\n" +
              "  \"pageable\": \"INSTANCE\",\n" +
              "  \"totalPages\": 1,\n" +
              "  \"totalElements\": 2,\n" +
              "  \"last\": true,\n" +
              "  \"size\": 5,\n" +
              "  \"number\": 0,\n" +
              "  \"sort\": {\n" +
              "    \"empty\": false,\n" +
              "    \"sorted\": true,\n" +
              "    \"unsorted\": false\n" +
              "  },\n" +
              "  \"first\": true,\n" +
              "  \"numberOfElements\": 2,\n" +
              "  \"empty\": false\n" +
              "}"
      ))
  )
  @GetMapping("/locandcat")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<StoreNearbyStoreWithCategoryResponseDto>> getNearbyStoresWithCategory(
      @RequestParam(value = "category") List<String> categoryNames,
      @RequestParam(value = "longitude") double longitude,
      @RequestParam(value = "latitude") double latitude,
      @SortDefault.SortDefaults({
          @SortDefault(sort = "createdAt", direction = Direction.ASC),
          @SortDefault(sort = "distanceFromRequest", direction = Direction.ASC)
      })
      @PageableDefault(
          size = 10,
          page = 0) Pageable pageable) {

    Page<StoreNearbyStoreWithCategoryResponseDto> storeDtos = storeService.findNearbyStoresByCategory(
        categoryNames, longitude, latitude, pageable);

    return ResponseEntity.ok().body(storeDtos);
  }

  @Operation(
      summary = "가게 정보 수정(가게 주인 기능)",
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
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  public ResponseEntity<String> updateStore(
      @RequestBody StoreUpdateRequestDto storeRequestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    storeService.updateStore(storeRequestDto, userDetails);

    return ResponseEntity.ok().body("가게 정보가 정상적으로 수정되었습니다.");
  }

  @Operation(
      summary = "가게 주문 내역 조회",
      description = "해당 가게의 주문 내역을 페이징 처리하여 조회하는 API",
      parameters = {
          @Parameter(
              name = "storeId",
              description = "주문 내역을 조회할 가게의 ID",
              required = true,
              example = "ff69173b-8401-4410-a9da-b503d1e2f6ff"
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
              example = "createdAt,desc"
          )
      }
  )
  @ApiResponse(
      responseCode = "200",
      description = "주문 내역 조회 성공",
      content = @Content(mediaType = "application/json", examples = @ExampleObject(
          name = "주문 내역 조회 성공 예시",
          value = "{\n" +
              "  \"content\": [\n" +
              "    {\n" +
              "      \"order_id\": \"orderUuid\",\n" +
              "      \"store_id\": \"ff69173b-8401-4410-a9da-b503d1e2f6ff\",\n" +
              "      \"order_date\": \"2024-10-25T14:30:00\",\n" +
              "      \"order_status\": \"COMPLETED\",\n" +
              "      \"total_amount\": 35000\n" +
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
  @GetMapping("/order")
  @PreAuthorize("hasAnyAuthority('ROLE_OWNER', 'ROLE_MASTER')")
  public ResponseEntity<Page<SearchOrderResponseDto>> getStoreOrders(
      @RequestParam(name = "storeId") String storeId,
      @PageableDefault(size = 10, page = 0,
          sort = {"createdAt", "updatedAt"},
          direction = Direction.ASC) Pageable pageable) {

    Page<SearchOrderResponseDto> orders = storeService.getOrdersByStore(storeId, pageable);

    return ResponseEntity.ok().body(orders);
  }
}
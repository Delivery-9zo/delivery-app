package com.sparta.deliveryapp.store.controller;

import com.sparta.deliveryapp.store.dto.StoreRequestDto;
import com.sparta.deliveryapp.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/")
public class StoreController {
    private final StoreService storeService;

    @PostMapping("/regi")
    public ResponseEntity<String> regiStore(@Valid @RequestBody StoreRequestDto storeRequestDto,
        BindingResult bindingResult) {
        //bindingresult check 로직 작성하기
        //owner 권한 체크하기
        storeService.regiStore(storeRequestDto);

        return ResponseEntity.ok().body("가게가 정상적으로 등록되었습니다.");
    }



}


/*
entity 만들기
Headers:Authorization: Bearer <token>
{
"store_uuid": "가게 uuid",
"sigungu_code": "12345",
"user_uuid": "owner uuid",
"store_name": "맛집",
"address": "서울특별시 종로구 세종대로 172",
"b_regi_num": "123-45-67890",
"open_at": 12:00,
"close_at": 22:00,
"category_uuid": "카테고리 uuid"
}
* */
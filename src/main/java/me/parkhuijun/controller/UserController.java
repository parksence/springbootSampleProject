package me.parkhuijun.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.parkhuijun.base.BaseResponse;
import me.parkhuijun.commons.Utils;
import me.parkhuijun.dto.TokenDTO;
import me.parkhuijun.dto.user.UserLoginRequests;
import me.parkhuijun.dto.user.UserSignUpRequest;
import me.parkhuijun.dto.user.UserSignUpResponse;
import me.parkhuijun.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "USER", description = "사용자 관련 API")
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "사용자 회원가입")
    @PostMapping("/user/signup")
    public ResponseEntity<BaseResponse> createUser(@RequestBody UserSignUpRequest request) {
        UserSignUpResponse data = userService.createUser(request);

        BaseResponse response = BaseResponse.builder()
                .code("OK")
                .data(Utils.asMap(data))
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "사용자 로그인")
    @PostMapping("/user/login")
    public ResponseEntity<BaseResponse> login(@RequestBody UserLoginRequests request) {

        TokenDTO tokenDTO = userService.login(request);

        BaseResponse response = BaseResponse.builder()
                .code("OK")
                .data(Utils.asMap(tokenDTO))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

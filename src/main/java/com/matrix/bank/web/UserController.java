package com.matrix.bank.web;

import com.matrix.bank.dto.ResponseDto;
import com.matrix.bank.dto.user.UserRespDto.JoinRespDto;
import com.matrix.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import static com.matrix.bank.dto.user.UserReqDto.JoinReqDto;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {

    private final UserService userService;

    // @RequestParam이 생략된 형태기 때문에 json으로 받으려면 @RequestBody를 붙여야 한다.
    // @Valid를 통과하지 못하면 모든 오류가 BindingResult에 담긴다.
    @PostMapping(("/join"))
    public ResponseEntity<?> join(@RequestBody @Valid JoinReqDto joinReqDto, BindingResult bindingResult) {



        JoinRespDto joinRespDto = userService.joinUser(joinReqDto);

        // HTTP status code 201 -> 뭔가가 하나 만들어졌다는 것
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입 성공", joinRespDto), HttpStatus.CREATED);
    }
}

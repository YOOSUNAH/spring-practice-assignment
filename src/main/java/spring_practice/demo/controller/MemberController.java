package spring_practice.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring_practice.demo.dto.LoginRequestDto;
import spring_practice.demo.dto.SignUpRequestDto;
import spring_practice.demo.global.ResponseDto;
import spring_practice.demo.service.MemberService;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Void>> signup(@Valid @RequestBody SignUpRequestDto signUpRequestDto){

        memberService.signup(signUpRequestDto);
        return ResponseDto.of(HttpStatus.OK, null);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Void>> login(@Valid @RequestBody LoginRequestDto loginRequestDto){

        memberService.login(loginRequestDto);

        return ResponseDto.of(HttpStatus.OK, null);

    }

}

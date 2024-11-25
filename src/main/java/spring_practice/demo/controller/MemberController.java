package spring_practice.demo.controller;

import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring_practice.demo.dto.LoginRequestDto;
import spring_practice.demo.dto.SignUpRequestDto;
import spring_practice.demo.dto.UpdatePasswordRequestDto;
import spring_practice.demo.global.ResponseDto;
import spring_practice.demo.service.MemberService;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Void>> signup(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        memberService.signup(signUpRequestDto);
        return ResponseDto.success(null);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Void>> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                   HttpServletRequest httpServletRequest,
                                                   HttpServletResponse httpServletResponse
    ) {
        memberService.login(loginRequestDto, httpServletRequest, httpServletResponse);
        return ResponseDto.success(null);
    }

    @PostMapping("/autoLogin")
    public ResponseEntity<ResponseDto<String>> autoLogin( @CookieValue(value = "cookieValue", required = false) String cookieValue, HttpServletRequest httpServletRequest) {
        memberService.autoLogin(cookieValue, httpServletRequest);
        return ResponseDto.success(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(HttpServletRequest httpServletRequest) {
        memberService.logout(httpServletRequest);
        return ResponseDto.success(null);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<ResponseDto<Void>> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto,
                                                            HttpServletRequest httpServletRequest){
        memberService.updatePassword(updatePasswordRequestDto, httpServletRequest);
        return ResponseDto.success(null);

    }



}



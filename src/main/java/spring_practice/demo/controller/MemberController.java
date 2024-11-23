package spring_practice.demo.controller;

import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring_practice.demo.dto.LoginRequestDto;
import spring_practice.demo.dto.SignUpRequestDto;
import spring_practice.demo.entity.Member;
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
        Member member = memberService.login(loginRequestDto, httpServletRequest, httpServletResponse);
        return ResponseDto.success(null);
    }

    // TODO: 로그인 요청 API에서 “로그인 유지” 관련 값을 같이 받아야한다. 세션 잘 생성된건지 확인해야한다.
//    @PostMapping("/keepLogin")
//    public ResponseEntity<ResponseDto<Void>> keepLogin(
//            @Valid @RequestBody LoginRequestDto loginRequestDto,
//            HttpServletResponse httpServletResponse)
//    {
//        memberService.keepLogin(loginRequestDto,httpServletResponse);
//        return ResponseDto.success(null);
//    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        memberService.logout(session);

        if(httpServletRequest.getCookies() == null){
            log.info("request에 cookie가 없습니다... ");
        }else{
            log.info("request에 cookie가 있습니다!! ");
        }

        if(httpServletRequest.getCookies() != null){
            Cookie[] cookie = httpServletRequest.getCookies();
            for(Cookie c : cookie){
                c.setMaxAge(0);
            }
        }

        if(httpServletRequest.getCookies() == null){
            log.info("request에 cookie가 없습니다... ");
        }

        return ResponseDto.success(null);
    }


}



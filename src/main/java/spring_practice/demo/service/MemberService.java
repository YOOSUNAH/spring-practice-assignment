package spring_practice.demo.service;

import groovy.util.logging.Slf4j;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring_practice.demo.dto.LoginRequestDto;
import spring_practice.demo.dto.SignUpRequestDto;
import spring_practice.demo.dto.UpdatePasswordRequestDto;
import spring_practice.demo.entity.Member;
import spring_practice.demo.repository.MemberRepository;

import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberPasswordUpdateValidator memberPasswordUpdateValidator;

    @Transactional
    public void signup(SignUpRequestDto signUpRequestDto) {
        // 이메일 중복 체크
        if (memberRepository.checkEmail(signUpRequestDto.email)) {
            throw new IllegalArgumentException("해당 이메일이 이미 존재합니다.");
        }
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.password);
        Member member = new Member(signUpRequestDto, encodedPassword);
        memberRepository.save(member);
    }


    @Transactional
    public void login(
        LoginRequestDto loginRequestDto,
        HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse
    ) {
        // 회원 확인
        Member member = readMember(loginRequestDto.email);

        // 로그인 시도 횟수 확인 및 시간 제한
        HttpSession session = httpServletRequest.getSession(true);
        int loginTrialCount = session.getAttribute("loginTrialCount") == null ? 0 : (int) session.getAttribute("loginTrialCount");

        if (exceedLimit(loginTrialCount) && exceedTime(session)) {
            throw new IllegalArgumentException("로그인 시도 횟수가 초과했습니다." + "60초 후에 다시 로그인해주세요.");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequestDto.password, member.getPassword())) {
            // 비밀번호 실패시, 실패 횟수 카운트, 마지막 로그인 시도 시간 저장
            recordLoginTrial(session, ++loginTrialCount);
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다." + (LoginConstants.LOGIN_FAIL_LIMIT_COUNT - loginTrialCount + 1) + "번의 기회가 남았습니다.");
        }

        // 세션 생성 및 정보 저장
        String memberEmail = member.getEmail();
        session.setAttribute("memberEmail", memberEmail);
        session.setMaxInactiveInterval(3600);

        // 유지하기 버튼을 눌렀으면 쿠키에도 저장
        if (loginRequestDto.keepLogin) {
            Cookie cookie = new Cookie("cookieValue", String.valueOf(member.getEmail()));
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);

            httpServletResponse.addCookie(cookie);
        }
    }

    private Member readMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityExistsException("해당 사용자가 존재하지 않습니다."));
    }

    public void recordLoginTrial(HttpSession session, int loginCount) {
        session.setAttribute("loginCount", loginCount);
        session.setAttribute("failTime", LocalTime.now());
    }

    public boolean exceedLimit(int loginCount) {
        return loginCount >= LoginConstants.LOGIN_FAIL_LIMIT_COUNT;
    }

    public boolean exceedTime(HttpSession session) {
        Object lastFailTime = session.getAttribute("failTime");
        LocalTime now = LocalTime.now();
        LocalTime failTime = lastFailTime == null
                ? now
                : (LocalTime) session.getAttribute("failTime");
        LocalTime leftTime = now.minusHours(failTime.getHour())
                .minusMinutes(failTime.getMinute())
                .minusSeconds(failTime.getSecond());
        // 60초동안 잠금
        LocalTime limitTime = LocalTime.of(0, 1);
        return leftTime.isBefore(limitTime);
    }

    @Transactional
    public void autoLogin(String cookieValue, HttpServletRequest httpServletRequest) {
        Member member = memberRepository.findByEmail(cookieValue).orElseThrow(
                () -> new EntityExistsException("해당 member 가 없습니다 .")
        );

        // 세션 생성 및 정보 저장
        HttpSession session = httpServletRequest.getSession(true);
        String memberEmail = member.getEmail();
        session.setAttribute("memberEmail", memberEmail);
        session.setMaxInactiveInterval(3600);
    }

    @Transactional
    public void logout(HttpServletRequest httpServletRequest) {
        // 로그인 유지하기 버튼을 눌러서 쿠키가 있다면 삭제
        deleteCookieIfKeepLogin(httpServletRequest.getCookies());

        // 세션 무효화
        HttpSession session = httpServletRequest.getSession();
        session.invalidate();
    }

    private void deleteCookieIfKeepLogin(Cookie[] cookies) {
        if (cookies == null) {
            return;
        }
        for (Cookie c : cookies) {
            c.setMaxAge(0);
        }
    }

    @Transactional
    public void updatePassword(
        UpdatePasswordRequestDto updatePasswordRequestDto,
        HttpServletRequest httpServletRequest
    ) {

        HttpSession session = httpServletRequest.getSession();
        String memberEmail = String.valueOf(session.getAttribute("memberEmail"));

        Member member = readMember(memberEmail);
        memberPasswordUpdateValidator.validateUpdatePassword(updatePasswordRequestDto, member.getPassword());

        // 비밀번호 변경하기 (인코딩하기)
        String encodedPassword = passwordEncoder.encode(updatePasswordRequestDto.getNewPassword());
        member.updatePassword(encodedPassword);

        // 세션 무효화하기 (로그 아웃 처리)
        logout(httpServletRequest);
    }

}

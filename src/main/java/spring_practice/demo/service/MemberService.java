package spring_practice.demo.service;

import groovy.util.logging.Slf4j;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    LocalTime now = LocalTime.now();

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
    public void login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 회원 확인
        Member member = memberRepository.findByEmail(loginRequestDto.email).orElseThrow(
                () -> new EntityExistsException("아이디가 일치하지 않습니다."));

        // 유지하기 버튼을 눌렀으면 쿠키에도 저장
        if (loginRequestDto.keepLogin) {
            Cookie cookie = new Cookie("cookieMemberId", String.valueOf(member.getMemberId()));
            httpServletResponse.addCookie(cookie);
        }

        // 세션 생성 및 정보 저장
        HttpSession session = httpServletRequest.getSession(true);

        String memberEmail = member.getEmail();
        session.setAttribute("memberEmail", memberEmail);
        session.setMaxInactiveInterval(3600);

        int loginCount = session.getAttribute("loginCount") == null ? 0 : (int) session.getAttribute("loginCount");
        int checkCount = 5;

        // 로그인 시도 횟수 확인 및 시간 제한
        if (!verifyExceedLimit(loginCount, checkCount) && !verifyExceedTime(session)) {
            throw new IllegalArgumentException("로그인 시도 횟수가 초과했습니다." + "60초 후에 다시 로그인해주세요.");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequestDto.password, member.getPassword())) {
            // 비밀번호 실패시, 실패 횟수 카운트, 마지막 로그인 시도 시간 저장
            recordLoginTrial(session, ++loginCount);
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다." + (checkCount - loginCount + 1) + "번의 기회가 남았습니다.");
        }

    }

    public void recordLoginTrial(HttpSession session, int loginCount) {
        session.setAttribute("loginCount", loginCount);
        session.setAttribute("failTime", now);
    }

    public boolean verifyExceedLimit(int loginCount, int checkCount) {
        return loginCount < checkCount;
    }

    public boolean verifyExceedTime(HttpSession session) {
        LocalTime failTime = session.getAttribute("failTime") == null ? LocalTime.now() : (LocalTime) session.getAttribute("failTime");
        now = LocalTime.now();
        LocalTime leftTime = now.minusHours(failTime.getHour())
                .minusMinutes(failTime.getMinute())
                .minusSeconds(failTime.getSecond());
        // 60초동안 잠금
        LocalTime limitTime = LocalTime.of(0, 1);
        return !leftTime.isBefore(limitTime);
    }

    @Transactional
    public void logout(HttpServletRequest httpServletRequest) {
        // 로그인 유지하기 버튼을 눌러서 쿠키가 있다면 삭제
        if (httpServletRequest.getCookies() != null) {
            Cookie[] cookie = httpServletRequest.getCookies();
            for (Cookie c : cookie) {
                c.setMaxAge(0);
            }
        }

        // 세션 무효화
        HttpSession session = httpServletRequest.getSession();
        session.invalidate();
    }

    @Transactional
    public void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto, HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession();
        String memberEmail = String.valueOf(session.getAttribute("memberEmail"));
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new EntityExistsException("member가 존재하지 않습니다. ")
        );
        // 기존 비밀번호 입력 : user가 맞는지 확인
        if (!passwordEncoder.matches(updatePasswordRequestDto.password, member.getPassword())) {
            throw new IllegalArgumentException("이전에 사용하던 비밀번호를 올바르게 입력해주세요 ");
        }
        // 새로운 비밀번호 입력 : 바꾸려는 비밀번호가 이전에 사용하던 것과 동일한지 확인
        if (passwordEncoder.matches(updatePasswordRequestDto.newPassword, member.getPassword())) {
            throw new IllegalArgumentException("이전에 사용하던 비밀번호와 일치합니다.");
        }

        // 새비밀번호와 확인비밀번호가 일치한지 확인
        if (!updatePasswordRequestDto.newPassword.equals(updatePasswordRequestDto.checkPassword)) {
            throw new IllegalArgumentException("바꾸려는 비밀번호와 동일하게 입력해주세요.");
        }

        // 비밀번호 변경하기 (인코딩하기)
        String encodedPassword = passwordEncoder.encode(updatePasswordRequestDto.getNewPassword());
        member.updatePassword(encodedPassword);

        // 세션 무효화하기 (로그 아웃 처리)
        logout(httpServletRequest);

    }
}

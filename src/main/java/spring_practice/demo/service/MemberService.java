package spring_practice.demo.service;

import groovy.util.logging.Slf4j;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring_practice.demo.dto.LoginRequestDto;
import spring_practice.demo.dto.SignUpRequestDto;
import spring_practice.demo.entity.Member;
import spring_practice.demo.repository.MemberRepository;

import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    LocalTime now = LocalTime.now();

    @Transactional
    public void signup(SignUpRequestDto signUpRequestDto) {
        // 이메일 중복 체크
        if (memberRepository.checkEmail(signUpRequestDto.email)) {
            throw new EntityExistsException("해당 이메일이 이미 존재합니다.");
        }
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.password);
        Member member = new Member(signUpRequestDto, encodedPassword);
        memberRepository.save(member);
    }

    @Transactional
    public Member login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest) {

        // 회원 확인
        Member member = memberRepository.findByEmail(loginRequestDto.email).orElseThrow(
                () -> new IllegalArgumentException("아이디가 일치하지 않습니다."));

        // 세션 생성 및 정보 저장
        HttpSession session = httpServletRequest.getSession(true);
//        String checkMemberEmail = session.getAttribute("memberEmail") == null ? "빈" : (String)session.getAttribute("memberEmail");
//        int checkLoginCount = session.getAttribute("loginCount") == null ? 0 : (int)session.getAttribute("loginCount");
//        LocalTime checkFailTime = session.getAttribute("failTime") == null ? LocalTime.now()  : (LocalTime) session.getAttribute("failTime");
//        log.info("세션 저장 확인 memberEmail {}", checkMemberEmail);
//        log.info("세션 저장 확인 loginCount {}", checkLoginCount);
//        log.info("세션 저장 확인 checkFailTime {}", checkFailTime);

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
        return member;
    }

    public void recordLoginTrial(HttpSession session, int loginCount) {
        session.setAttribute("loginCount", loginCount);
        session.setAttribute("failTime", now);
    }

    public boolean verifyExceedLimit(int loginCount, int checkCount) {
        return loginCount >= checkCount ? false : true;
    }

    public boolean verifyExceedTime(HttpSession session) {
        LocalTime failTime = session.getAttribute("failTime") == null ? LocalTime.now() : (LocalTime) session.getAttribute("failTime");
//        log.info("실패최종시간 {}", failTime);

        now = LocalTime.now();
//        log.info("현재시간 {}", now);
        LocalTime leftTime = now.minusHours(failTime.getHour())
                .minusMinutes(failTime.getMinute())
                .minusSeconds(failTime.getSecond());

//        log.info("실패최종시간-현재시간 {}", leftTime);

        LocalTime limitTime = LocalTime.of(0, 1);

        return !leftTime.isBefore(limitTime);
    }
}

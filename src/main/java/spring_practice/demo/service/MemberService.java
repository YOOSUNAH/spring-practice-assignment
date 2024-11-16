package spring_practice.demo.service;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring_practice.demo.dto.LoginRequestDto;
import spring_practice.demo.dto.SignUpRequestDto;
import spring_practice.demo.entity.Member;
import spring_practice.demo.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void signup(SignUpRequestDto signUpRequestDto) {
        // 이메일 중복 체크
        if(memberRepository.checkEmail(signUpRequestDto.email)){
            throw new EntityExistsException("해당 이메일이 이미 존재합니다.");
        }

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.password);
        Member member = new Member(signUpRequestDto,  encodedPassword);
        memberRepository.save(member);
    }

    @Transactional
    public Member login(LoginRequestDto loginRequestDto) {
        // 회원 확인
        Member member = memberRepository.findByEmail(loginRequestDto.email).orElseThrow(
                () -> new IllegalArgumentException("아이디가 일치하지 않습니다."));
        // 비밀번호 확인
        if(!passwordEncoder.matches(loginRequestDto.password, member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;



    }
}

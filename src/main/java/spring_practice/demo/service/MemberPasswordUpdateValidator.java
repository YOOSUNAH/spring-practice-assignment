package spring_practice.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring_practice.demo.dto.UpdatePasswordRequestDto;

@Service
@RequiredArgsConstructor
public class MemberPasswordUpdateValidator {

    private final PasswordEncoder passwordEncoder;

    void validateUpdatePassword(UpdatePasswordRequestDto updatePasswordRequestDto, String password) {
        // 기존 비밀번호 입력 : user가 맞는지 확인
        if (!passwordEncoder.matches(updatePasswordRequestDto.password, password)) {
            throw new IllegalArgumentException("이전에 사용하던 비밀번호를 올바르게 입력해주세요 ");
        }
        // 새로운 비밀번호 입력 : 바꾸려는 비밀번호가 이전에 사용하던 것과 동일한지 확인
        if (passwordEncoder.matches(updatePasswordRequestDto.newPassword, password)) {
            throw new IllegalArgumentException("이전에 사용하던 비밀번호와 일치합니다.");
        }

        // 새비밀번호와 확인비밀번호가 일치한지 확인
        if (!updatePasswordRequestDto.newPassword.equals(updatePasswordRequestDto.checkPassword)) {
            throw new IllegalArgumentException("바꾸려는 비밀번호와 동일하게 입력해주세요.");
        }
    }
}
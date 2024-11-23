package spring_practice.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring_practice.demo.dto.SignUpRequestDto;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;

    private String password;

    private String name;

    private String phoneNumber;

    private boolean keepLogin;

    public Member(SignUpRequestDto signUpRequestDto, String password){
        this.email = signUpRequestDto.email;
        this.password = password;
        this.name = signUpRequestDto.name;
        this.phoneNumber = signUpRequestDto.phoneNumber;
        this.keepLogin = false;
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }

}

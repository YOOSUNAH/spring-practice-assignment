package spring_practice.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "이메일은 필수 입력값입니다. ")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    public String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다. ")
    public String password;

    public boolean keepLogin;


}

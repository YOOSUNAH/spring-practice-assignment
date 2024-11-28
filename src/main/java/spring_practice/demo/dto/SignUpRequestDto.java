package spring_practice.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank(message = "이메일은 필수 입력값입니다. ")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    public String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다. ")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
            message = "비밀번호 형식이 올바르지 않습니다. 8자 이상, 대소문자 포함, 숫자 및 특수문자 포함")
    public String password;

    @NotBlank(message = "이름은 필수 입력값입니다. ")
    public String name;

    @NotBlank(message = "전화번호는 필수 입력값입니다. ")
    @Pattern(regexp = "^[0-9]*$")
    public String phoneNumber;

}

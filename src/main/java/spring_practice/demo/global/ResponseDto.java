package spring_practice.demo.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {

    private T data;
    private String message;

    public static <T> ResponseEntity<ResponseDto<T>> of (
            HttpStatus status,
            T data
    ){
        return ResponseEntity.status(status).body(new ResponseDto<>(data, null));
    }

    public static <T> ResponseEntity<ResponseDto<T>> success (
            T data
    ){
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(data, "성공"));
    }

    public static <T> ResponseEntity<ResponseDto<T>> fail (
            T data
    ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(data, "실패"));
    }


}

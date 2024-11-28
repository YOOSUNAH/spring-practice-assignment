package spring_practice.demo.formVSjson;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormRequest {

    private User user;
    private Order order;

    @Getter
    @Setter
    public static class User {
        @NotEmpty(message = "Name is required")
        private String name;

        @Email(message = "Invalid email format")
        private String email;
    }

    @Getter
    @Setter
    public static class Order {

        private String orderId;
        private Service service;

        @Getter
        @Setter
        public static class Service {

            @NotBlank(message = "serviceId is required")
            private String serviceId;

            @NotBlank(message = "serviceName is required")
            private String serviceName;

            private int quantity;

            private double price;

        }


    }




}

package spring_practice.demo.formVSjson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JsonResponse {

    private String name;
    private String email;
    private String orderId;
    private String serviceId;
    private String serviceName;
    private int quantity;
    private double price;

}

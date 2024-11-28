package spring_practice.demo.formVSjson;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/form")
public class FormController {

    @PostMapping("/submit2")
    public String handleFormRequest2(
            @ModelAttribute FormRequest formRequest,
            Model model
    ){
        String name  = formRequest.getUser().getName();
        String email  = formRequest.getUser().getEmail();
        String orderId= formRequest.getOrder().getOrderId();

        FormRequest.Order.Service service = formRequest.getOrder().getService();
        String serviceId =  service.getServiceId();
        String serviceName = service.getServiceName();
        int quantity =  service.getQuantity();
        double price =  service.getPrice();

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("orderId", orderId);
        model.addAttribute("serviceId", serviceId);
        model.addAttribute("serviceName",serviceName);
        model.addAttribute("quantity",quantity );
        model.addAttribute("price", price );

        return "formResponse"; // formResponse.html 템플릿을 렌더링
    }


    // 단일 파라미터 받기
    @PostMapping("/submit")
    public String handleFormRequest(
            @RequestParam("name") String name,
            @RequestParam("age") int age,
            Model model
    ){

            model.addAttribute("name", name);
            model.addAttribute("age", age);

            return "formResponse"; // formResponse.html 템플릿을 렌더링
    }

    // 객체로 매핑하기
    @PostMapping("/submitObject")
    public String handleFormRequestAsObject(
            @ModelAttribute UseForm useForm, Model model
    ){
        model.addAttribute("user", useForm);

        return "formResponse";
    }


}

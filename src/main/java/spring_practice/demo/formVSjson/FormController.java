package spring_practice.demo.formVSjson;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/form")
public class FormController {

    @PostMapping("/submit2")
    public String handleFormRequest2(
            @ModelAttribute FormRequestEntity formRequestEntity,
            Model model,
            HttpServletRequest request
    ){
        String length  = request.getHeader("Content-Length");
        System.out.println("폼요청 - Content-Length : " + length + "byte");


        String name  = formRequestEntity.getUser().getName();
        String email  = formRequestEntity.getUser().getEmail();
        String orderId= formRequestEntity.getOrder().getOrderId();

        FormRequestEntity.Order.Service service = formRequestEntity.getOrder().getService();
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

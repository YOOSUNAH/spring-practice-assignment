package spring_practice.demo.formVSjson;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/json")
public class JsonController {

    @PostMapping("/submit")
    public String jsonRequest(
            @RequestBody JsonRequestEntity jsonRequestEntity,
            HttpSession session,
            HttpServletRequest request
    ){
        String length  = request.getHeader("Content-Length");
        System.out.println("json 요청 - Content-Length : " + length + "byte");

        String name = jsonRequestEntity.getUser().getName();
        String email = jsonRequestEntity.getUser().getEmail();
        String orderId = jsonRequestEntity.getOrder().getOrderId();

        JsonRequestEntity.Order.Service service = jsonRequestEntity.getOrder().getService();
        String serviceId = service.getServiceId();
        String serviceName = service.getServiceName();
        int quantity = service.getQuantity();
        double price = service.getPrice();

        JsonResponse jsonResponse = new JsonResponse(name, email, orderId, serviceId, serviceName, quantity, price);

        session.setAttribute("jsonResponse", jsonResponse);

        return "jsonResponse";
    }

    @GetMapping("/getData")
    @ResponseBody
    public Map<String, Object> getJsonData(
            HttpSession httpSession) {

        // 세션에서 jsonResponse 데이터 가져오기
        JsonResponse jsonResponse = (JsonResponse) httpSession.getAttribute("jsonResponse");

        Map<String, Object> response = new HashMap<>();

        if (jsonResponse != null) {
            response.put("jsonResponse", jsonResponse);
        } else {
            response.put("message", "no data");
        }
        return response;
    }

    @GetMapping("/getData2")
    public ResponseEntity<Map<String, Object>> getJsonData2(HttpSession session) {
        JsonResponse jsonResponse = (JsonResponse) session.getAttribute("jsonResponse");

        Map<String, Object> response = new HashMap<>();
        if (jsonResponse != null) {
            response.put("jsonResponse", jsonResponse);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No data available.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



}


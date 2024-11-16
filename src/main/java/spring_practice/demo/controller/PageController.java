package spring_practice.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController implements ErrorController {

    @GetMapping("/member/signup")
    public String signUpPage(){
        return "signup";
    }

    @GetMapping("/homePage")
    public String homePage(){
        return "homePage";
    }

    @GetMapping("/myPage")
    public String myPage(){
        return "myPage";
    }

}

package spring_practice.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/updatePassword")
    public String updatePassword(){
        return "updatePassword";
    }


    @GetMapping("/formTest")
    public String formTest(){
        return "formTest";
    }

    @GetMapping("/jsonTest")
    public String jsonTest(){
        return "jsonTest";
    }

    @GetMapping("/jsonResponse")
    public String jsonResponse(){
        return "jsonResponse";
    }

    @GetMapping("/fileUpload")
    public String fileUpload(){
        return "/fileUpload";
    }

    @GetMapping("/fileResult")
    public String fileResult(){
        return "/fileResult";
    }




}

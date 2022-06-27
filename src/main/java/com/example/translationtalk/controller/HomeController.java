package com.example.translationtalk.controller;

import com.example.translationtalk.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
public class HomeController {

    @Autowired
    UserInfoService userInfoService;

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60*60*24); // 세션 만료 기간 : 1일
        Object accessTokenObj = session.getAttribute("accessToken");
        if(accessTokenObj!=null) {
            String nickname = userInfoService.getUserNickname(accessTokenObj.toString());
            if(nickname=="error") return "error";
            model.addAttribute("login", true);
            model.addAttribute("nickname", nickname);
        }
        return "home";
    }


    @GetMapping("/logout")
    public String logout(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:home";
    }
}

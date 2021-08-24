package com.example.translationtalk.controller;

import com.example.translationtalk.service.GetTranslatedTextService;
import com.example.translationtalk.service.GetUserInfoService;
import com.example.translationtalk.service.makemsg.TextMsgService;
import com.example.translationtalk.service.sendfriend.SendFriendMsgService;
import com.example.translationtalk.service.sendme.SendMeMsgService;
import com.example.translationtalk.service.token.AccessTokenService;
import com.example.translationtalk.service.token.RefreshTokenService;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(60*60*24); // 세션 만료 기간 : 1일
        Object accessTokenObj = session.getAttribute("accessToken");

        if(accessTokenObj!=null) {
            GetUserInfoService getUserInfoService = new GetUserInfoService();
            String nickname = getUserInfoService.getUserNickname(accessTokenObj.toString());
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

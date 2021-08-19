package com.example.translationtalk.controller;

import com.example.translationtalk.SaveAC;
import com.example.translationtalk.service.GetUserInfoService;
import com.example.translationtalk.service.sendfriend.GetFriendsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model) {
        if(SaveAC.accessToken.length()!=0) {
            GetUserInfoService getUserInfoService = new GetUserInfoService();
            String nickname = getUserInfoService.getUserNickname(SaveAC.accessToken);
            model.addAttribute("login", true);
            model.addAttribute("nickname", nickname);
        }
        return "home";
    }


    @GetMapping("/sendoptions")
    public String sendOptions(Model model){
        GetFriendsService getFriendsService = new GetFriendsService();
        getFriendsService.getFriends(SaveAC.accessToken);

        return "sendoptions";
    }


    @GetMapping("/logout")
    public String logout(Model model) {
        SaveAC.accessToken="";
        return "redirect:home";
    }
}

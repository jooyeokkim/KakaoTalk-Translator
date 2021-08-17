package com.example.translationtalk.controller;

import com.example.translationtalk.SaveAC;
import com.example.translationtalk.service.sendfriend.GetFriendsService;
import com.example.translationtalk.service.GetTranslatedTextService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model) {
        return "home";
    }


    @GetMapping("/sendoptions")
    public String sendOptions(Model model){
        GetFriendsService getFriendsService = new GetFriendsService();
        getFriendsService.getFriends(SaveAC.accessToken);

        return "sendoptions";
    }


    @GetMapping("/starttalk")
    public String startTalk(@RequestParam("korean") String korean, Model model) {
        String text = korean;
        
        GetTranslatedTextService getTranslatedTextService = new GetTranslatedTextService();
        String translatedText = getTranslatedTextService.getTranslatedText(text);

        model.addAttribute("translatedText",translatedText);

        System.out.println(translatedText);

        return "success";
    }
}

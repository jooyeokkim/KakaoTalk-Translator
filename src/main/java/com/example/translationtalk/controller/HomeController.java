package com.example.translationtalk.controller;

import com.example.translationtalk.SaveAC;
import com.example.translationtalk.makemsg.TextMsg;
import com.example.translationtalk.service.GetFriendsService;
import com.example.translationtalk.service.GetTranslatedTextService;
import com.example.translationtalk.service.GetUserInfoService;
import com.example.translationtalk.service.AccessTokenService;
import org.json.JSONObject;
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


    @GetMapping("/sendOptions")
    public String sendOptions(Model model){
        GetFriendsService getFriendsService = new GetFriendsService();
        getFriendsService.getFriends(SaveAC.accessToken);

        return "sendoptions";
    }


    @GetMapping("/friendList")
    public String friendList(@RequestParam("code") String code, Model model){
        String redirect_uri = "http://kimcoder.kro.kr:8080/friendList";

        AccessTokenService accessTokenService = new AccessTokenService();

        SaveAC.accessToken = accessTokenService.getAccessToken(code, redirect_uri);
        if(SaveAC.accessToken=="error") return "error";

        GetFriendsService getFriendsService = new GetFriendsService();
        getFriendsService.getFriends(SaveAC.accessToken);

        return "choosefriend";
    }


    @GetMapping("/startTalk")
    public String startTalk(@RequestParam("korean") String korean, Model model) {
        String text = korean;
        
        GetTranslatedTextService getTranslatedTextService = new GetTranslatedTextService();
        String translatedText = getTranslatedTextService.getTranslatedText(text);

        model.addAttribute("translatedText",translatedText);

        System.out.println(translatedText);

        return "success";
    }
}

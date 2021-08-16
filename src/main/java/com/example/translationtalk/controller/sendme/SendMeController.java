package com.example.translationtalk.controller.sendme;

import com.example.translationtalk.SaveAC;
import com.example.translationtalk.controller.HomeController;
import com.example.translationtalk.makemsg.TextMsg;
import com.example.translationtalk.service.AccessTokenService;
import com.example.translationtalk.service.GetTranslatedTextService;
import com.example.translationtalk.service.sendme.SendMsgService;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sendMe")
public class SendMeController {

    @GetMapping("/enterMsg")
    public String enterMsg(@RequestParam("code") String code, Model model){
        String redirect_uri = "http://kimcoder.kro.kr:8080/sendMe/enterMsg";

        AccessTokenService accessTokenService = new AccessTokenService();

        SaveAC.accessToken = accessTokenService.getAccessToken(code, redirect_uri);
        if(SaveAC.accessToken=="error") return "error";
        return "sendMe/entermsg";
    }


    @GetMapping("/send")
    public String send(@RequestParam("message") String message, Model model){
        GetTranslatedTextService getTranslatedTextService = new GetTranslatedTextService();
        String translatedText = getTranslatedTextService.getTranslatedText(message);

        TextMsg textMsg = new TextMsg();
        JSONObject template_object = textMsg.getTextMsg(translatedText);

        SendMsgService sendMsgService = new SendMsgService();
        String result = sendMsgService.sendMsg(SaveAC.accessToken, template_object);
        System.out.println(result);
        if(result=="error") return "error";

        return "home";
    }
}

package com.example.translationtalk.controller.sendme;

import com.example.translationtalk.SaveAC;
import com.example.translationtalk.makemsg.TextMsg;
import com.example.translationtalk.service.AccessTokenService;
import com.example.translationtalk.service.GetTranslatedTextService;
import com.example.translationtalk.service.sendme.SendMeMsgService;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sendme")
public class SendMeController {

    @GetMapping("/recieveac")
    public String recieveac(@RequestParam("code") String code, Model model){
        String redirect_uri = "http://kimcoder.kro.kr:8080/sendme/recieveac";

        AccessTokenService accessTokenService = new AccessTokenService();

        SaveAC.accessToken = accessTokenService.getAccessToken(code, redirect_uri);
        if(SaveAC.accessToken=="error") return "error";

        return "redirect:entermsg";
    }


    @GetMapping("/entermsg")
    public String enterMsg(Model model){
        return "sendme/entermsg";
    }


    @GetMapping("/send")
    public String send(@RequestParam("message") String message, Model model){
        GetTranslatedTextService getTranslatedTextService = new GetTranslatedTextService();
        String translatedText = getTranslatedTextService.getTranslatedText(message);

        TextMsg textMsg = new TextMsg();
        JSONObject template_object = textMsg.getTextMsg(translatedText);

        SendMeMsgService sendMeMsgService = new SendMeMsgService();
        String result = sendMeMsgService.sendMsg(SaveAC.accessToken, template_object);
        System.out.println(result);
        if(result=="error") return "error";

        return "home";
    }
}

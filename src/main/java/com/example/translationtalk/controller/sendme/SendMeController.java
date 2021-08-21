package com.example.translationtalk.controller.sendme;

import com.example.translationtalk.service.makemsg.TextMsgService;
import com.example.translationtalk.service.AccessTokenService;
import com.example.translationtalk.service.GetTranslatedTextService;
import com.example.translationtalk.service.sendme.SendMeMsgService;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/sendme")
public class SendMeController {


    @GetMapping("/recieveac")
    public String recieveac(@RequestParam("code") String code, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String redirect_uri = "http://kimcoder.kro.kr:8080/sendme/recieveac";

        AccessTokenService accessTokenService = new AccessTokenService();

        String accessToken = accessTokenService.getAccessToken(code,redirect_uri);

        session.setMaxInactiveInterval(60*60*6);
        session.setAttribute("accessToken", accessToken);

        if(accessToken=="error") return "error";

        return "redirect:entermsg";
    }


    @GetMapping("/entermsg")
    public String enterMsg(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        model.addAttribute("message", session.getAttribute("message"));
        model.addAttribute("translatedMessage", session.getAttribute("translatedMessage"));
        return "sendme/entermsg";
    }


    @GetMapping("/send")
    public String send(@RequestParam("message") String message,
                       @RequestParam("language") String language, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String accessToken = session.getAttribute("accessToken").toString();

        GetTranslatedTextService getTranslatedTextService = new GetTranslatedTextService();
        String translatedText = getTranslatedTextService.getTranslatedText(message, language);

        TextMsgService textMsgService = new TextMsgService();
        JSONObject template_object = textMsgService.getTextMsg(translatedText);

        SendMeMsgService sendMeMsgService = new SendMeMsgService();

        String result = sendMeMsgService.sendMsg(accessToken, template_object);
        System.out.println(result);
        if(result=="error") return "error";

        session.setAttribute("message", message);
        session.setAttribute("translatedMessage", translatedText);
        return "redirect:entermsg";
    }
}

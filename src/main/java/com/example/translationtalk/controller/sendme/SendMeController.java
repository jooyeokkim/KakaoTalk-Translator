package com.example.translationtalk.controller.sendme;

import com.example.translationtalk.service.makemsg.TextMsgService;
import com.example.translationtalk.service.token.AccessTokenService;
import com.example.translationtalk.service.TranslatedTextService;
import com.example.translationtalk.service.sendme.SendMeMsgService;
import com.example.translationtalk.service.token.RefreshTokenService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/sendme")
public class SendMeController {

    @Autowired AccessTokenService accessTokenService;
    @Autowired RefreshTokenService refreshTokenService;
    @Autowired TextMsgService textMsgService;
    @Autowired SendMeMsgService sendMeMsgService;
    @Autowired
    TranslatedTextService translatedTextService;

    @GetMapping("/receiveac")
    public String receiveac(@RequestParam("code") String code, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();

        Map<String, String> tokens = accessTokenService.getAccessToken(code, "http://kimcoder.kro.kr:8080/sendme/receiveac");
        if(tokens==null) return "error";
        System.out.println("Send me access token = "+tokens.get("accessToken")); // For RestTemplateTest
        System.out.println("Send me refresh token = "+tokens.get("refreshToken")); // For RestTemplateTest

        session.setMaxInactiveInterval(60*60*24);
        session.setAttribute("accessToken", tokens.get("accessToken"));
        session.setAttribute("refreshToken", tokens.get("refreshToken"));

        return "redirect:entermsg";
    }


    @GetMapping("/entermsg")
    public String enterMsg(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        model.addAttribute("message", session.getAttribute("message"));
        model.addAttribute("translatedMessage", session.getAttribute("translatedMessage"));
        return "sendme/entermsg";
    }


    @PostMapping("/send")
    public String send(@RequestParam("message") String message,
                       @RequestParam("language") String language, Model model, HttpServletRequest request){

        HttpSession session = request.getSession();


        // 세션 만료 확인
        Object accessToken = session.getAttribute("accessToken");
        Object refreshToken = session.getAttribute("refreshToken");
        if(accessToken==null||refreshToken==null) return "expired";


        // 엑세스 토큰 유효 검사
        String checkResult = accessTokenService.checkAccessTokenState(accessToken.toString());

        if(checkResult=="expired"){ // 만료 시 refresh
            Map<String, String> tokens = refreshTokenService.refresh(refreshToken.toString());
            if(tokens==null) return "error";

            session.setAttribute("accessToken", tokens.get("accessToken"));

            Object newRefreshToken = tokens.get("refreshToken");
            if(newRefreshToken!=null){
                session.setAttribute("refreshToken", newRefreshToken.toString());
            }
        } else if(checkResult=="error") return "error";


        // 구글 번역 api 사용
        String translatedText = translatedTextService.getTranslatedText(message, language);
        if(translatedText=="error") return "error";

        // 텍스트 템플릿 작성
        JSONObject template_object = textMsgService.getTextMsg(translatedText);

        // 메세지 전송
        String sendResult = sendMeMsgService.sendMsg(accessToken.toString(), template_object);
        System.out.println(sendResult);
        if(sendResult=="error") return "error";

        session.setAttribute("message", message);
        session.setAttribute("translatedMessage", translatedText);
        return "redirect:entermsg";
    }
}

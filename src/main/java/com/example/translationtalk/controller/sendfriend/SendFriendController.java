package com.example.translationtalk.controller.sendfriend;

import com.example.translationtalk.service.makemsg.TextMsgService;
import com.example.translationtalk.service.token.AccessTokenService;
import com.example.translationtalk.service.sendfriend.FriendsService;
import com.example.translationtalk.service.TranslatedTextService;
import com.example.translationtalk.service.sendfriend.SendFriendMsgService;
import com.example.translationtalk.service.token.RefreshTokenService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;

@Controller
@RequestMapping("/sendfriend")
public class SendFriendController {

    @Autowired AccessTokenService accessTokenService;
    @Autowired RefreshTokenService refreshTokenService;
    @Autowired TextMsgService textMsgService;
    @Autowired FriendsService friendsService;
    @Autowired SendFriendMsgService sendFriendMsgService;
    @Autowired TranslatedTextService translatedTextService;

    @GetMapping("/receiveac")
    public String receiveac(@RequestParam("code") String code, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();

        Map<String, String> tokens = accessTokenService.getAccessToken(code, "http://kimcoder.kro.kr:8080/sendfriend/receiveac");
        if(tokens==null) return "error";
        System.out.println("Send friend access token = "+tokens.get("accessToken")); // For RestTemplateTest
        System.out.println("Send friend refresh token = "+tokens.get("refreshToken")); // For RestTemplateTest

        session.setMaxInactiveInterval(60*60*24);
        session.setAttribute("accessToken", tokens.get("accessToken"));
        session.setAttribute("refreshToken", tokens.get("refreshToken"));

        return "redirect:choosefriend?page=1";
    }


    @GetMapping("/choosefriend")
    public String chooseFriend(@RequestParam("page") String page,
                               Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        final int limitPage = 100;
        int page_int = Integer.parseInt(page);

        Object accessToken = session.getAttribute("accessToken");
        if(accessToken==null) return "expired";

        //친구 수 추출
        int totalCount = friendsService.getTotalCount(accessToken.toString(), 100, 0);
        if(totalCount==-1) return "error";

        //해당 page의 사용자 목록 추출
        if(totalCount<=limitPage){
            model.addAttribute("previous", false);
            model.addAttribute("next", false);
        }
        else if(page_int==1){
            model.addAttribute("previous", false);
            model.addAttribute("next", true);
        }
        else if(page_int==(int)Math.ceil((double)totalCount/limitPage)){
            model.addAttribute("previous", true);
            model.addAttribute("next", false);
        }
        else{
            model.addAttribute("previous", true);
            model.addAttribute("next", true);
        }
        model.addAttribute("previousPage", page_int-1);
        model.addAttribute("nextPage",page_int+1);
        ArrayList<Map<String, String>> friends = friendsService.getFriendsMap(accessToken.toString(),limitPage,(page_int-1)*limitPage);
        if(friends==null) return "error";

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("friends", friends);
        return "sendfriend/choosefriend";
    }


    @PostMapping("/saveusercode")
    public String saveUsercode(@RequestParam("usercode") String usercode, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();

        String[] splitedUsercode = usercode.split("&");
        String nickname = splitedUsercode[0];
        String uuid = splitedUsercode[1];
        session.setAttribute("nickname", nickname);
        session.setAttribute("uuid", uuid);
        return "redirect:entermsg";
    }


    @GetMapping("/entermsg")
    public String enterMsg(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();

        Object nickname = session.getAttribute("nickname");
        if(nickname==null) return "expired";

        model.addAttribute("nickname", nickname);
        model.addAttribute("message", session.getAttribute("message"));
        model.addAttribute("translatedMessage", session.getAttribute("translatedMessage"));

        return "/sendfriend/entermsg";
    }


    @PostMapping("/send")
    public String send(@RequestParam("message") String message,
                       @RequestParam("language") String language, Model model, HttpServletRequest request){

        HttpSession session = request.getSession();

        // 세션 만료 확인
        Object accessToken = session.getAttribute("accessToken");
        Object uuid = session.getAttribute("uuid");
        Object refreshToken = session.getAttribute("refreshToken");
        if(accessToken==null||uuid==null||refreshToken==null) return "expired";


        // 엑세스 토큰 유효 검사
        String checkResult = accessTokenService.checkAccessTokenState(accessToken.toString());

        if(checkResult=="expired"){ // 만료 시 refresh
            Map<String, String> tokens = refreshTokenService.refresh(refreshToken.toString());
            if(tokens==null) return "error";

            session.setAttribute("accessToken", tokens.get("accessToken"));

            //System.out.println("새로운 access_token" + session.getAttribute("accessToken"));
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
        String result = sendFriendMsgService.sendMsg(accessToken.toString(), uuid.toString(), template_object);
        System.out.println(result);
        if(result=="error") return "error";

        session.setAttribute("message", message);
        session.setAttribute("translatedMessage", translatedText);
        return "redirect:entermsg";
    }
}

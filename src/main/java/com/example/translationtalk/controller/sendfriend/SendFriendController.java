package com.example.translationtalk.controller.sendfriend;

import com.example.translationtalk.service.makemsg.TextMsgService;
import com.example.translationtalk.service.token.AccessTokenService;
import com.example.translationtalk.service.sendfriend.GetFriendsService;
import com.example.translationtalk.service.GetTranslatedTextService;
import com.example.translationtalk.service.sendfriend.SendFriendMsgService;
import com.example.translationtalk.service.token.RefreshTokenService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sendfriend")
public class SendFriendController {

    @GetMapping("/receiveac")
    public String receiveac(@RequestParam("code") String code, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String redirect_uri = "http://kimcoder.kro.kr:8080/sendfriend/receiveac";

        AccessTokenService accessTokenService = new AccessTokenService();
        Map<String, String> tokens = accessTokenService.getAccessToken(code, redirect_uri);

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
        GetFriendsService getFriendsService = new GetFriendsService();
        String jsonData = getFriendsService.getFriends(accessToken.toString(),100,0);
        JSONObject friendsJsonObject = new JSONObject(jsonData);
        int totalCount = (int)friendsJsonObject.get("total_count");


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
        jsonData = getFriendsService.getFriends(accessToken.toString(),limitPage,(page_int-1)*limitPage);
        friendsJsonObject = new JSONObject(jsonData);


        //사용자들의 닉네임과 프로필 사진 추출
        ArrayList<Map<String, String>> friends = new ArrayList<Map<String, String>>();
        JSONArray elementsJSONArray = (JSONArray) friendsJsonObject.get("elements");

        for(int i=0; i<elementsJSONArray.length(); i++){
            JSONObject friend = (JSONObject) elementsJSONArray.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("nickname", friend.get("profile_nickname").toString());
            if(friend.get("profile_thumbnail_image").toString().length()!=0)
                map.put("thumbnail", friend.get("profile_thumbnail_image").toString());
            else map.put("thumbnail", null);
            map.put("uuid", friend.get("uuid").toString());
            friends.add(map);
        }


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
        AccessTokenService accessTokenService = new AccessTokenService();
        String checkResult = accessTokenService.checkAccessTokenState(accessToken.toString());

        if(checkResult=="expired"){ // 만료 시 refresh
            RefreshTokenService refreshTokenService = new RefreshTokenService();
            Map<String, String> tokens = refreshTokenService.refresh(refreshToken.toString());
            session.setAttribute("accessToken", tokens.get("accessToken"));

            //System.out.println("새로운 access_token" + session.getAttribute("accessToken"));
            Object newRefreshToken = tokens.get("refreshToken");
            if(newRefreshToken!=null){
                session.setAttribute("refreshToken", newRefreshToken.toString());
            }
        } else if(checkResult=="error") return "error";


        // 구글 번역 api 사용
        GetTranslatedTextService getTranslatedTextService = new GetTranslatedTextService();
        String translatedText = getTranslatedTextService.getTranslatedText(message, language);


        // 텍스트 템플릿 작성
        TextMsgService textMsgService = new TextMsgService();
        JSONObject template_object = textMsgService.getTextMsg(translatedText);


        // 메세지 전송
        SendFriendMsgService sendMsgService = new SendFriendMsgService();
        String result = sendMsgService.sendMsg(accessToken.toString(), uuid.toString(), template_object);
        System.out.println(result);
        if(result=="error") return "error";


        session.setAttribute("message", message);
        session.setAttribute("translatedMessage", translatedText);
        return "redirect:entermsg";
    }
}

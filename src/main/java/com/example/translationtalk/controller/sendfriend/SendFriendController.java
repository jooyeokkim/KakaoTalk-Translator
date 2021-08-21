package com.example.translationtalk.controller.sendfriend;

import com.example.translationtalk.service.makemsg.TextMsgService;
import com.example.translationtalk.service.AccessTokenService;
import com.example.translationtalk.service.sendfriend.GetFriendsService;
import com.example.translationtalk.service.GetTranslatedTextService;
import com.example.translationtalk.service.sendfriend.SendFriendMsgService;
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
    private String uuid;
    private String nickname;
    private int total_count;
    private ArrayList<Map<String, String>> friends;


    @GetMapping("/recieveac")
    public String recieveac(@RequestParam("code") String code, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();

        String redirect_uri = "http://kimcoder.kro.kr:8080/sendfriend/recieveac";

        AccessTokenService accessTokenService = new AccessTokenService();

        String accessToken = accessTokenService.getAccessToken(code, redirect_uri);
        session.setAttribute("accessToken", accessToken);
        if(accessToken=="error") return "error";

        return "redirect:choosefriend";
    }


    @GetMapping("/choosefriend")
    public String chooseFriend(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String accessToken = session.getAttribute("accessToken").toString();

        GetFriendsService getFriendsService = new GetFriendsService();
        String jsonData = getFriendsService.getFriends(accessToken);

        //JSON String -> JSON Object
        JSONObject friendsJsonObject = new JSONObject(jsonData);

        //친구 수 추출
        total_count = (int)friendsJsonObject.get("total_count");

        //사용자의 닉네임과 프로필 사진 추출
        friends = new ArrayList<Map<String, String>>();
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

        model.addAttribute("total_count", total_count);
        model.addAttribute("friends", friends);
        return "sendfriend/choosefriend";
    }


    @PostMapping("/saveusercode")
    public String getUsercode(@RequestParam("usercode") String usercode, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("usercode", usercode);
        return "redirect:entermsg";
    }


    @GetMapping("/entermsg")
    public String enterMsg(Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        String usercode = session.getAttribute("usercode").toString();

        String[] splitedUsercode = usercode.split("&");
        nickname = splitedUsercode[0];
        uuid = splitedUsercode[1];

        model.addAttribute("nickname", nickname);
        model.addAttribute("uuid", uuid);
        model.addAttribute("message", session.getAttribute("message"));
        model.addAttribute("translatedMessage", session.getAttribute("translatedMessage"));

        return "/sendfriend/entermsg";
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

        SendFriendMsgService sendMsgService = new SendFriendMsgService();
        String result = sendMsgService.sendMsg(accessToken, uuid, template_object);

        System.out.println(result);
        if(result=="error") return "error";

        session.setAttribute("message", message);
        session.setAttribute("translatedMessage", translatedText);
        return "redirect:entermsg";
    }
}

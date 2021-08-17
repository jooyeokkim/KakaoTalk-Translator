package com.example.translationtalk.controller.sendfriend;

import com.example.translationtalk.SaveAC;
import com.example.translationtalk.makemsg.TextMsg;
import com.example.translationtalk.service.AccessTokenService;
import com.example.translationtalk.service.sendfriend.GetFriendsService;
import com.example.translationtalk.service.GetTranslatedTextService;
import com.example.translationtalk.service.sendme.SendMeMsgService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sendfriend")
public class SendFriendController {
    String receiver_uuids[];

    @GetMapping("/recieveac")
    public String recieveac(@RequestParam("code") String code, Model model){
        String redirect_uri = "http://kimcoder.kro.kr:8080/sendfriend/recieveac";

        AccessTokenService accessTokenService = new AccessTokenService();

        SaveAC.accessToken = accessTokenService.getAccessToken(code, redirect_uri);
        if(SaveAC.accessToken=="error") return "error";

        return "redirect:choosefriend";
    }

    @GetMapping("/choosefriend")
    public String chooseFriend(Model model){
        int total_count;

        GetFriendsService getFriendsService = new GetFriendsService();
        String jsonData = getFriendsService.getFriends(SaveAC.accessToken);

        //JSON String -> JSON Object
        JSONObject friendsJsonObject = new JSONObject(jsonData);

        //친구 수 추출
        total_count = (int)friendsJsonObject.get("total_count");

        //사용자의 닉네임과 프로필 사진 추출
        ArrayList<Map<String, String>> friends = new ArrayList<Map<String, String>>();
        JSONArray elementsJSONArray = (JSONArray) friendsJsonObject.get("elements");
        for(int i=0; i<elementsJSONArray.length(); i++){
            JSONObject friend = (JSONObject) elementsJSONArray.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("nickname", friend.get("profile_nickname").toString());
            map.put("thumbnail", friend.get("profile_thumbnail_image").toString());
            map.put("uuid", friend.get("uuid").toString());
            friends.add(map);
        }
        model.addAttribute("total_count", total_count);
        model.addAttribute("friends", friends);
        return "sendfriend/choosefriend";
    }

    @GetMapping("/entermsg")
    public String enterMsg(@RequestParam("code") String code, Model model){
        String redirect_uri = "http://kimcoder.kro.kr:8080/sendme/entermsg";

        AccessTokenService accessTokenService = new AccessTokenService();

        SaveAC.accessToken = accessTokenService.getAccessToken(code, redirect_uri);
        if(SaveAC.accessToken=="error") return "error";
        return "/sendfriend/entermsg";
    }


    @GetMapping("/send")
    public String send(@RequestParam("message") String message, Model model){
        GetTranslatedTextService getTranslatedTextService = new GetTranslatedTextService();
        String translatedText = getTranslatedTextService.getTranslatedText(message);

        TextMsg textMsg = new TextMsg();
        JSONObject template_object = textMsg.getTextMsg(translatedText);

        SendMeMsgService sendMsgService = new SendMeMsgService();
        String result = sendMsgService.sendMsg(SaveAC.accessToken, template_object);
        System.out.println(result);
        if(result=="error") return "error";

        return "/home";
    }
}

package com.example.translationtalk.service.makemsg;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class TextMsgService {
    public JSONObject getTextMsg(String message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("object_type", "text");
        jsonObject.put("text", message);

        JSONObject link = new JSONObject();
        link.put("web_url", "http://kimcoder.kro.kr:8080/home");
        link.put("mobile_web_url", "http://kimcoder.kro.kr:8080/home");

        jsonObject.put("link", link);
        jsonObject.put("button_title", "홈으로 이동");

        return jsonObject;
    }
}

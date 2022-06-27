package com.example.translationtalk.service;

import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class UserInfoService {
    private final String HTTP_REQUEST = "https://kapi.kakao.com/v2/user/me";

    @Autowired
    MyRestTemplate myRestTemplate;

    public String getUserNickname(String accessToken) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(HTTP_REQUEST);
        String jsonData=myRestTemplate.getJsonData(accessToken, MediaType.APPLICATION_FORM_URLENCODED, uriComponentsBuilder, HttpMethod.POST);
        if(jsonData=="error") return "error";

        //JSON String -> JSON Object
        JSONObject userInfoJsonObject = new JSONObject(jsonData);

        //유저의 닉네임 추출
        JSONObject propertiesJSONObject = (JSONObject)userInfoJsonObject.get("properties");
        String nickname = propertiesJSONObject.get("nickname").toString();
        return nickname;
    }
}

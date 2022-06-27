package com.example.translationtalk.service.sendme;

import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SendMeMsgService {
    private final String HTTP_REQUEST = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

    @Autowired
    MyRestTemplate myRestTemplate;

    public String sendMsg(String accessToken, JSONObject jsonObject){
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(HTTP_REQUEST)
                .queryParam("template_object", jsonObject.toString());
        String jsonData=myRestTemplate.getJsonData(accessToken, MediaType.APPLICATION_JSON, uriComponentsBuilder, HttpMethod.POST);
        if(jsonData=="error") return "error";
        return jsonData;
    }
}

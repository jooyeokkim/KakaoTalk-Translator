package com.example.translationtalk.service.sendfriend;

import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SendFriendMsgService {
    private final String HTTP_REQUEST = "https://kapi.kakao.com/v1/api/talk/friends/message/default/send";

    @Autowired
    MyRestTemplate myRestTemplate;

    public String sendMsg(String accessToken, String uuid, JSONObject jsonObject){
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(HTTP_REQUEST)
                .queryParam("receiver_uuids", "[\""+uuid+"\"]")
                .queryParam("template_object", jsonObject.toString());
        String jsonData=myRestTemplate.getJsonData(accessToken, MediaType.APPLICATION_FORM_URLENCODED, uriComponentsBuilder, HttpMethod.POST);
        if(jsonData=="error") return "error";
        return jsonData;
    }
}

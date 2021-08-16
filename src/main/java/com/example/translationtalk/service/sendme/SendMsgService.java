package com.example.translationtalk.service.sendme;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class SendMsgService {
    private final String HTTP_REQUEST = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private String jsonResult = "";

    public String sendMsg(String accessToken, JSONObject jsonObject){
        RestTemplate restTemplate = new RestTemplate();
        String url = HTTP_REQUEST + "?access_token={access_token}&template_object={template_object}";

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", accessToken);
        map.put("template_object", jsonObject.toString());

        HttpHeaders headers = new HttpHeaders();
        Charset utf8 = Charset.forName("UTF-8");
        MediaType mediaType = new MediaType("application","json", utf8);
        headers.setContentType(mediaType);
        HttpEntity request = new HttpEntity(headers);

        try{
            jsonResult = restTemplate.postForObject(url, request, String.class, map);
        } catch (Exception e){
            e.printStackTrace();
        }

        return jsonResult;
    }
}

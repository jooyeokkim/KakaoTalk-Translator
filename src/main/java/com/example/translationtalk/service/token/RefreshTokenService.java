package com.example.translationtalk.service.token;

import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class RefreshTokenService {
    private final String GRANT_TYPE= "refresh_token";
    private final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.client.secret}")
    private String clientSecret;
    @Autowired
    MyRestTemplate myRestTemplate;

    public Map<String, String> refresh(String refreshToken){
        Map<String, String> tokens = new HashMap<String, String>();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("grant_type", GRANT_TYPE)
                .queryParam("client_id", clientId)
                .queryParam("refresh_token", refreshToken)
                .queryParam("client_secret", clientSecret);

        String jsonData=myRestTemplate.getJsonData("",MediaType.APPLICATION_FORM_URLENCODED, uriComponentsBuilder, HttpMethod.POST);
        if(jsonData=="error") return null;

        //JSON String -> JSON Object
        JSONObject accessTokenJsonObject = new JSONObject(jsonData);

        //token 추출 후 저장
        String newAccessToken = accessTokenJsonObject.get("access_token").toString();
        tokens.put("accessToken", newAccessToken);
        try{
            Object newRefreshToken = accessTokenJsonObject.get("refresh_token");
            if(newRefreshToken!=null){
                tokens.put("refreshToken", newRefreshToken.toString());
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return tokens;
    }
}

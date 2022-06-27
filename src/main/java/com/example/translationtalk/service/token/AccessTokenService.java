package com.example.translationtalk.service.token;

import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccessTokenService {
    private final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String TOKEN_STATE_URL = "https://kapi.kakao.com/v1/user/access_token_info";
    private final String GRANT_TYPE= "authorization_code";
    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Autowired
    MyRestTemplate myRestTemplate;

    public Map<String, String> getAccessToken(String code, String redirect_uri){
        Map<String, String> tokens = new HashMap<String, String>();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("grant_type", GRANT_TYPE)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirect_uri)
                .queryParam("code", code)
                .queryParam("client_secret", clientSecret);
        String jsonData=myRestTemplate.getJsonData("", MediaType.APPLICATION_FORM_URLENCODED, uriComponentsBuilder, HttpMethod.POST);
        if(jsonData=="error") return null;

        //JSON String -> JSON Object
        JSONObject accessTokenJsonObject = new JSONObject(jsonData);

        //token 추출 후 저장
        String accessToken = accessTokenJsonObject.get("access_token").toString();
        String refreshToken = accessTokenJsonObject.get("refresh_token").toString();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }


    public String checkAccessTokenState(String accessToken){
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(TOKEN_STATE_URL);
        try {
            String jsonData=myRestTemplate.getJsonData(accessToken,MediaType.APPLICATION_FORM_URLENCODED, uriComponentsBuilder, HttpMethod.GET);
            if(jsonData=="error") return "error";
            else return "success";
        } catch (HttpClientErrorException.Unauthorized ue){
            return "expired";
        }
    }
}

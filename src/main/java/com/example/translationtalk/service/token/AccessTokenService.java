package com.example.translationtalk.service.token;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


public class AccessTokenService {
    private final String GRANT_TYPE= "authorization_code";
    private final String CLIENT_ID = "YOUR_CLIENT_ID";
    private final String CLIENT_SECRET= "YOUR_CLIENT_SECRET";
    private final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String TOKEN_STATE_URL = "https://kapi.kakao.com/v1/user/access_token_info";
    private String accessTokenJsonData = "";

    public Map<String, String> getAccessToken(String code, String redirect_uri){
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> tokens = new HashMap<String, String>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity(headers);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("grant_type", GRANT_TYPE)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", redirect_uri)
                .queryParam("code", code)
                .queryParam("client_secret", CLIENT_SECRET);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.POST,
                request,
                String.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            accessTokenJsonData = responseEntity.getBody();

            //JSON String -> JSON Object
            JSONObject accessTokenJsonObject = new JSONObject(accessTokenJsonData);

            //token 추출 후 저장
            String accessToken = accessTokenJsonObject.get("access_token").toString();
            String refreshToken = accessTokenJsonObject.get("refresh_token").toString();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
        }

        return tokens;
    }


    public String checkAccessTokenState(String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity(headers);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(TOKEN_STATE_URL)
                .queryParam("access_token", accessToken);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    uriComponentsBuilder.toUriString(),
                    HttpMethod.GET,
                    request,
                    String.class
            );
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException.Unauthorized ue){
            return "expired";
        }
        return "error";
    }
}

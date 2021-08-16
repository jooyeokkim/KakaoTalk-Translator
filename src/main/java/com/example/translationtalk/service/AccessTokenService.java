package com.example.translationtalk.service;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


public class AccessTokenService {
    private final String GRANT_TYPE= "authorization_code";
    private final String CLIENT_ID = "50ca5e8cf40713abcab868ed9ed3047d";
    private final String CLIENT_SECRET= "Jh4Y0e5IS5IWCoCKzTfnQUQX8okqCSC0";
    private final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private String accessTokenJsonData = "";

    public String getAccessToken(String code, String redirect_uri){
        RestTemplate restTemplate = new RestTemplate();

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

            //access_token 추출
            String accessToken = accessTokenJsonObject.get("access_token").toString();

            return accessToken;
        }
        return "error";
    }
}

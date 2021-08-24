package com.example.translationtalk.service.token;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

public class RefreshTokenService {
    private final String GRANT_TYPE= "refresh_token";
    private final String CLIENT_ID = "50ca5e8cf40713abcab868ed9ed3047d";
    private final String CLIENT_SECRET= "Jh4Y0e5IS5IWCoCKzTfnQUQX8okqCSC0";
    private final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private String accessTokenJsonData = "";

    public Map<String, String> refresh(String refreshToken){
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> tokens = new HashMap<String, String>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity(headers);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("grant_type", GRANT_TYPE)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("refresh_token", refreshToken)
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
            String newAccessToken = accessTokenJsonObject.get("access_token").toString();
            tokens.put("accessToken", newAccessToken);
            try{
                Object newRefreshToken = accessTokenJsonObject.get("refresh_token");
                if(newRefreshToken!=null){
                    tokens.put("refreshToken", newRefreshToken.toString());
                }
            } catch (JSONException e){}
        }
        return tokens;
    }
}

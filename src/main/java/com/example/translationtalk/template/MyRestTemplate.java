package com.example.translationtalk.template;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class MyRestTemplate {

    // Kakao, Google에게 요청하는 RestTemplate 방식이 일치하기 때문에 같은 메소드를 사용하나, 방식이 달라질 경우 요청 서버마다 메소드를 분리하는 것을 권장함.
    public String getJsonData(String accessToken, MediaType mediaType, UriComponentsBuilder uriComponentsBuilder, HttpMethod httpMethod) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setBearerAuth(accessToken);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    uriComponentsBuilder.build().encode().toUri(),
                    httpMethod,
                    request,
                    String.class
            );
        } catch (HttpClientErrorException.Unauthorized ue){
            throw ue;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        if(responseEntity.getStatusCode()==HttpStatus.OK) return responseEntity.getBody();
        else return "error";
    }
}

package com.example.translationtalk.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class GetTranslatedTextService {
    private final String KEY = "AIzaSyAJpD7ogqW_dGJ2uhEM8F0Jr-aBdRRAzXw";
    private final String REQUEST_URL = "https://translation.googleapis.com/language/translate/v2";

    public String getTranslatedText(String text){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.googleapis.com/language/translate/v2?key={key}&q={q}&source={source}&target={target}";

        Map<String, String> map = new HashMap<String, String>();
        map.put("key", KEY);
        map.put("q", text);
        map.put("source", "ko");
        map.put("target", "en");

        HttpHeaders headers = new HttpHeaders();
        Charset utf8 = Charset.forName("UTF-8");
        MediaType mediaType = new MediaType("application","json", utf8);
        headers.setContentType(mediaType);
        HttpEntity request = new HttpEntity(headers);

        String jsonResult = restTemplate.postForObject(url, request, String.class, map);
        jsonResult = jsonResult.replace("&#39;","'");

        // JSON 파싱 작업
        JSONObject translatedTextJsonObject = new JSONObject(jsonResult);
        JSONObject dataJsonObject = (JSONObject)translatedTextJsonObject.get("data");
        JSONArray translationsJsonArray = (JSONArray)dataJsonObject.get("translations");
        JSONObject translationsJsonObject = (JSONObject)translationsJsonArray.get(0);
        String translatedText = translationsJsonObject.get("translatedText").toString();

        return translatedText;
    }
}

//    RestTemplate restTemplate = new RestTemplate();
//
//    String encodedText = "";
//        try{
//                encodedText = URLEncoder.encode(text, "UTF-8");
//                } catch(UnsupportedEncodingException e){
//                e.printStackTrace();
//                }
//
//                Map<String, Object> params = new HashMap<>();
//        params.put("key", KEY);
//        params.put("source", "ko");
//        params.put("target", "en");
//        params.put("q", encodedText);
//
//        System.out.println(encodedText);
//
//        ResponseEntity<String> responseEntity =
//        restTemplate.postForEntity(REQUEST_URL, params, String.class);
//
//        if (responseEntity.getStatusCode() == HttpStatus.OK) {
//        return responseEntity.getBody();
//        }
//        return "error";






//    RestTemplate restTemplate = new RestTemplate();
//
//    HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//                HttpEntity request = new HttpEntity(headers);
//
//                UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(REQUEST_URL)
//                .queryParam("key", KEY)
//                .queryParam("source", "ko")
//                .queryParam("target", "en")
//                .queryParam("q", text);
//
//                ResponseEntity<String> responseEntity = restTemplate.exchange(
//        uriComponentsBuilder.toUriString(),
//        HttpMethod.POST,
//        request,
//        String.class
//        );
//
//                if (responseEntity.getStatusCode() == HttpStatus.OK) {
//                return responseEntity.getBody();
//                }
//                return "error";

//    private final String KEY = "AIzaSyAJpD7ogqW_dGJ2uhEM8F0Jr-aBdRRAzXw";
//    private final String REQUEST_URL = "https://translation.googleapis.com/language/translate/v2";
//
//    public String getTranslatedTextJsonData(String text){
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "https://www.googleapis.com/language/translate/v2?key={key}&q={q}&source={source}&target={target}";
//
//
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("key", KEY);
//        map.put("q", text);
//        map.put("source", "ko");
//        map.put("target", "en");
//
//        String jsonResult = restTemplate.getForObject(url, String.class, map);
//        System.out.println(jsonResult);
//        return jsonResult;
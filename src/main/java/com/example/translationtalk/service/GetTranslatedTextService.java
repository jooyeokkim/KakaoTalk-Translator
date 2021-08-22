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

    public String getTranslatedText(String text, String targetLanguage){
        RestTemplate restTemplate = new RestTemplate();
        String url = REQUEST_URL + "?key={key}&q={q}&source={source}&target={target}";

        Map<String, String> map = new HashMap<String, String>();
        map.put("key", KEY);
        map.put("q", text);
        map.put("source", "ko");
        map.put("target", targetLanguage);

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
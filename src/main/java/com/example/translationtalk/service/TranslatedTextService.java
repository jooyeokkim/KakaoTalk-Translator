package com.example.translationtalk.service;

import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TranslatedTextService {
    private final String REQUEST_URL = "https://translation.googleapis.com/language/translate/v2";
    @Value("${google.api.key}")
    private String key;
    @Autowired
    MyRestTemplate myRestTemplate;

    public String getTranslatedText(String text, String targetLanguage){
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(REQUEST_URL)
                .queryParam("key", key)
                .queryParam("q", text)
                .queryParam("source", "ko")
                .queryParam("target", targetLanguage);
        String jsonData=myRestTemplate.getJsonData("", MediaType.APPLICATION_JSON, uriComponentsBuilder, HttpMethod.POST);
        if(jsonData=="error") return "error";

        jsonData = jsonData.replace("&#39;","'");
        // 번역된 text 추출
        JSONObject translatedTextJsonObject = new JSONObject(jsonData);
        JSONObject dataJsonObject = (JSONObject)translatedTextJsonObject.get("data");
        JSONArray translationsJsonArray = (JSONArray)dataJsonObject.get("translations");
        JSONObject translationsJsonObject = (JSONObject)translationsJsonArray.get(0);
        String translatedText = translationsJsonObject.get("translatedText").toString();

        return translatedText;
    }
}
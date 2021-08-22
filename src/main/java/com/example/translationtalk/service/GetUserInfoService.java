package com.example.translationtalk.service;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GetUserInfoService {
    private final String HTTP_REQUEST = "https://kapi.kakao.com/v2/user/me";

    public String getUserNickname(String accessToken){
        try {
            String jsonData = "";

            //

            // URI를 URL객체로 저장
            URL url = new URL(HTTP_REQUEST + "?access_token=" + accessToken);

            // 버퍼 데이터(응답 메세지)를 한 줄씩 읽어서 jsonData에 저장
            BufferedReader bf;
            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while((line = bf.readLine()) != null){
                jsonData+=line;
            }

            //JSON String -> JSON Object
            JSONObject userInfoJsonObject = new JSONObject(jsonData);

            //유저의 닉네임 추출
            JSONObject propertiesJSONObject = (JSONObject)userInfoJsonObject.get("properties");
            String nickname = propertiesJSONObject.get("nickname").toString();

            return nickname;

        } catch(IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

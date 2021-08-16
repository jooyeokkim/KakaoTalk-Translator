package com.example.translationtalk.service;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class GetFriendsService {
    private final String HTTP_REQUEST = "https://kapi.kakao.com/v1/api/talk/friends";
    private int limit = 50;
    private int offset = 0;

    public String getFriends(String accessToken){
        try {
            String jsonData = "";

            // URI를 URL객체로 저장
            URL url = new URL(HTTP_REQUEST +
                    "?access_token=" + accessToken +
                    "&limit=" + limit);

            // 버퍼 데이터(응답 메세지)를 한 줄씩 읽어서 jsonData에 저장
            BufferedReader bf;
            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while((line = bf.readLine()) != null){
                jsonData+=line;
            }
            System.out.println(accessToken);
            System.out.println(jsonData);

//            //JSON String -> JSON Object
//            JSONObject userInfoJsonObject = new JSONObject(jsonData);
//
//            //유저의 ID 추출
//            JSONObject idJsonObject = (JSONObject)userInfoJsonObject.get("id");
//            String userId = idJsonObject.toString();
//
//            return userId;
            return "success";
        } catch(Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}

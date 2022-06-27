package com.example.translationtalk.service.sendfriend;

import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class FriendsService {
    private final String HTTP_REQUEST = "https://kapi.kakao.com/v1/api/talk/friends";

    @Autowired
    MyRestTemplate myRestTemplate;

    public ArrayList<Map<String, String>> getFriendsMap(String accessToken, int limit, int offset){
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(HTTP_REQUEST)
                .queryParam("limit", limit)
                .queryParam("offset", offset);
        String jsonData=myRestTemplate.getJsonData(accessToken, MediaType.APPLICATION_JSON, uriComponentsBuilder, HttpMethod.GET);
        if(jsonData=="error") return null;

        //JSON String -> JSON Object
        JSONObject friendsJsonObject = new JSONObject(jsonData);

        //사용자들의 닉네임과 프로필 사진 추출
        ArrayList<Map<String, String>> friends = new ArrayList<Map<String, String>>();
        JSONArray elementsJSONArray = (JSONArray) friendsJsonObject.get("elements");

        for(int i=0; i<elementsJSONArray.length(); i++){
            JSONObject friend = (JSONObject) elementsJSONArray.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("nickname", friend.get("profile_nickname").toString());
            if(friend.get("profile_thumbnail_image").toString().length()!=0)
                map.put("thumbnail", friend.get("profile_thumbnail_image").toString());
            else map.put("thumbnail", null);
            map.put("uuid", friend.get("uuid").toString());
            friends.add(map);
        }
        return friends;
    }

    public int getTotalCount(String accessToken, int limit, int offset){
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(HTTP_REQUEST)
                .queryParam("limit", limit)
                .queryParam("offset", offset);
        String jsonData=myRestTemplate.getJsonData(accessToken, MediaType.APPLICATION_JSON, uriComponentsBuilder, HttpMethod.GET);
        if(jsonData=="error") return -1;

        //JSON String -> JSON Object
        JSONObject friendsJsonObject = new JSONObject(jsonData);

        //친구 수 추출
        int totalCount = (int)friendsJsonObject.get("total_count");
        return totalCount;
    }
}

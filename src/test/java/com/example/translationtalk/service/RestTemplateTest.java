package com.example.translationtalk.service;

import com.example.translationtalk.service.makemsg.TextMsgService;
import com.example.translationtalk.service.sendfriend.FriendsService;
import com.example.translationtalk.service.sendme.SendMeMsgService;
import com.example.translationtalk.service.token.AccessTokenService;
import com.example.translationtalk.service.token.RefreshTokenService;
import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Map;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTemplateTest {
    private String sendMeAccessToken="RcKczQmj3aj5DMC7bLShLJ_LJpxDzUIArgXhCd9rCilvVQAAAYGuCeAm";
    private String sendMeRefreshToken="xYTth8pFbR7mD4ZTDZXfWFWTEGP3yxJC2YD0TvevCilvVQAAAYGuCeAk";
    private String sendFriendAccessToken="jM-fxiOaR9IYvIX5y5wxpnvdRZPNFy5Gkd5LkeKlCilvVQAAAYGuCemg";
    private String sendFriendRefreshToken="mKjk9QtZ6RWEaFec88w9ky8FEnVGU7M1vnEJiquwCilvVQAAAYGuCeme";
    private String expiredAccessToken="vSqgYVB5hpeCPEwwvnhGe1O0e0kwJaulgcGtP5DkCj1z6wAAAYGfO8-D";

    @Autowired FriendsService friendsService;
    @Autowired TextMsgService textMsgService;
    @Autowired SendMeMsgService sendMeMsgService;
    @Autowired AccessTokenService accessTokenService;
    @Autowired MyRestTemplate myRestTemplate;
    @Autowired RefreshTokenService refreshTokenService;
    @Autowired TranslatedTextService translatedTextService;
    @Autowired UserInfoService userInfoService;

    @Test
    public void friendsServiceTest() {
        ArrayList<Map<String, String>> friendsMap=friendsService.getFriendsMap(sendFriendAccessToken, 100, 0);
        assertNotNull(friendsMap);
        int totalCount=friendsService.getTotalCount(sendFriendAccessToken, 100, 0);
        assertNotEquals(-1, totalCount);
    }

    @Test
    public void sendMeMsgServiceTest(){
        JSONObject template_object = textMsgService.getTextMsg("test");
        String response=sendMeMsgService.sendMsg(sendMeAccessToken, template_object);
        assertNotEquals("error", response);
    }

    @Test(expected = HttpClientErrorException.Unauthorized.class)
    public void expiredAccessTokenTest() {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("https://kapi.kakao.com/v1/user/access_token_info");
        myRestTemplate.getJsonData(
                expiredAccessToken,
                MediaType.APPLICATION_FORM_URLENCODED,
                uriComponentsBuilder,
                HttpMethod.GET
        );
    }

    @Test
    public void failToGetAccessTokenTest() {
        String invaildCode="1234567890";
        Map<String, String> tokens = accessTokenService.getAccessToken(invaildCode, "http://kimcoder.kro.kr:8080/sendfriend/receiveac");
        assertNull(tokens);
    }

    @Test
    public void refreshTest() {
        Map<String, String> tokens = refreshTokenService.refresh(sendMeRefreshToken);
        assertNotNull(tokens);
        tokens = refreshTokenService.refresh(sendFriendRefreshToken);
        assertNotNull(tokens);
        String invaildRefreshToken="1234567890";
        tokens = refreshTokenService.refresh(invaildRefreshToken);
        assertNull(tokens);
    }

    @Test
    public void translatedTextTest() {
        String source="안녕하세요";
        String targetLanguage="en";
        String invalidTargetLanguage="abcdefg";
        String translatedText = translatedTextService.getTranslatedText(source, targetLanguage);
        assertNotEquals("_error", translatedText);
        String errorText = translatedTextService.getTranslatedText(source, invalidTargetLanguage);
        assertEquals("_error", errorText);
    }

    @Test
    public void userInfoServiceTest() {
        String nickName=userInfoService.getUserNickname(sendMeAccessToken);
        assertNotEquals("_error", nickName);
        nickName=userInfoService.getUserNickname(sendFriendAccessToken);
        assertNotEquals("_error", nickName);
    }
}

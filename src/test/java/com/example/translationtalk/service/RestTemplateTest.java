package com.example.translationtalk.service;

import com.example.translationtalk.service.makemsg.TextMsgService;
import com.example.translationtalk.service.sendfriend.FriendsService;
import com.example.translationtalk.service.sendfriend.SendFriendMsgService;
import com.example.translationtalk.service.sendme.SendMeMsgService;
import com.example.translationtalk.service.token.AccessTokenService;
import com.example.translationtalk.service.token.RefreshTokenService;
import com.example.translationtalk.template.MyRestTemplate;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTemplateTest {
    private String sendMeAccessToken="jvcX-kRenqux5E1d18SMCNu5CvvMvyidKAB25K04CilwUAAAAYGjpave";
    private String sendMeRefreshToken="0K5oErRXRkOTLftsEFydKLVDDr2IW5kl5cgDIFY1CilwUAAAAYGjpavc";
    private String sendFriendAccessToken="9LIkeSCXle2RQQOrn9DaIDYEtyUSYKtm5aV72T38CilwUQAAAYGjpe-U";
    private String sendFriendRefreshToken="t1xm3Nay9vzA6K4DEvNKlmdIJJDcmbzNvSCuXYaHCilwUQAAAYGjpe-T";
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
        assertNotNull(friendsService);
        ArrayList<Map<String, String>> friendsMap=friendsService.getFriendsMap(sendFriendAccessToken, 100, 0);
        assertNotNull(friendsMap);
        int totalCount= friendsService.getTotalCount(sendFriendAccessToken, 100, 0);
        assertTrue(totalCount!=-1);
    }

    @Test
    public void sendMeMsgServiceTest(){
        JSONObject template_object = textMsgService.getTextMsg("test");
        String response=sendMeMsgService.sendMsg(sendMeAccessToken, template_object);
        assertTrue(response!="error");
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
}

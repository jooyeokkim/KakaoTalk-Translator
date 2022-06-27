var client_id="50ca5e8cf40713abcab868ed9ed3047d";
var root_url="http%3A%2F%2Fkimcoder.kro.kr%3A8080%2F"; // http://kimcoder.kro.kr:8080/

function sendFriend(){
    location.href = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+root_url+"sendfriend%2Freceiveac&scope=friends,talk_message,profile_nickname";
}

function sendMe(){
    location.href = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+root_url+"sendme%2Freceiveac&scope=talk_message,profile_nickname";
}

function logout(){
    location.href = "https://kauth.kakao.com/oauth/logout?client_id="+client_id+"&logout_redirect_uri="+root_url+"logout";
}
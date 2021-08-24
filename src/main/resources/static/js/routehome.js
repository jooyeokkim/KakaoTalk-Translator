function sendFriend(){
    location.href = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=50ca5e8cf40713abcab868ed9ed3047d&redirect_uri=http%3A%2F%2Fkimcoder.kro.kr%3A8080%2Fsendfriend%2Freceiveac&scope=friends,talk_message,profile_nickname";
}

function sendMe(){
    location.href = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=50ca5e8cf40713abcab868ed9ed3047d&redirect_uri=http%3A%2F%2Fkimcoder.kro.kr%3A8080%2Fsendme%2Freceiveac&scope=talk_message,profile_nickname";
}

function logout(){
    location.href = "https://kauth.kakao.com/oauth/logout?client_id=50ca5e8cf40713abcab868ed9ed3047d&logout_redirect_uri=http%3A%2F%2Fkimcoder.kro.kr%3A8080%2Flogout";
}
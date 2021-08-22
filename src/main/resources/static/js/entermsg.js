function moveHome(){
    location.href = "/home";
}

var sendBtn = document.querySelector('#send');

$(document).ready(function(){
    sendBtn.disabled = true;
});

$('#text').keyup(function (e){
    var content = $(this).val();
    $('#showLength').html("현재 "+content.length+"자 / 최대 30자");

    if (content.length > 0){
        sendBtn.disabled = false;
    } else {
        sendBtn.disabled = true;
    }

    if (content.length > 30){
        alert("최대 30자까지 입력 가능합니다.");
        $(this).val(content.substring(0, 30));
        $('#showLength').html("(현재 30자 / 최대 30자)");
    }
});
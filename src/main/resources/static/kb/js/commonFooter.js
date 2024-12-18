// 쿠폰함, 쿠폰 히스토리 페이지 X 버튼 동작
function closeWindow(){
    self.opener = self;
    self.close();
}

$(document).ready(function () {
    startTimer();
})

document.addEventListener("click", startTimer);
document.addEventListener("keydown", startTimer);

let timer;
function startTimer() {
    // 기존 타이머가 존재한다면 초기화
    if (timer) {
        clearTimeout(timer);
    }

    // 새 타이머 설정 (예: 5분 후 경고)
    timer = setTimeout(() => {
        const hiddenInput = document.querySelectorAll('input[type="hidden"]');
        hiddenInput.forEach(input => input.value='');
        window.location = "/page/kb/error.ux";
    }, 300000); // 5분 = 300,000ms - 300000
}


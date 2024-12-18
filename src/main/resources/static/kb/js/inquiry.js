$(document).ready(function () {
    // 문의 유형 선택 동작
    document.querySelectorAll('.selectInquiry')
        .forEach(function (item) {
            item.addEventListener('click', function () {
                // 클릭한 항목의 텍스트를 가져와서 넘긴 문의 유형(type)에 value를 변경하기
                document.getElementById('selectInput').value = this.textContent;
            });
        });
})

// 글자수 카운트
function updateCharCount() {
    const textarea = document.getElementById('inquiryContent');
    const charCount = textarea.value.length;
    const charCountDisplay = document.getElementById('charCount');

    charCountDisplay.textContent = `${charCount} / 500`;

    if (charCount > 500) {
        charCountDisplay.classList.add('exceeded');
    } else {
        charCountDisplay.classList.remove('exceeded');
    }
}

let inquiryCheck = false;

// 문의 등록
function insertInquiry() {

    // 문의 내용이 하나라도 정상적이지 않을 경우 false
    if (document.getElementById('inquiryTitle').value.length === 0 ||
        document.getElementById('selectInput').value.length === 0 ||
        document.getElementById('inquiryContent').value.length === 0) {
        inquiryCheck = false;
    }else{ // 문의 내용이 전부 정상적으로 작성되었을 경우 true;
        inquiryCheck = true;
    }

    // 문의 내용 정상 작성 시 동작 처리
    if(inquiryCheck){
        const resultText = document.getElementById("resultText").value;

        fetch("/inquiry/insertInquiry.do", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                resultText: resultText,
                inquiryTitle: document.getElementById("inquiryTitle").value,
                inquiryContent: document.getElementById("inquiryContent").value,
                type: document.getElementById("selectInput").value,
                memberId: document.getElementById("memberId").value
            })
        })
            .then(response => response.json())
            .then((data) => {
                // 정상적으로 문의가 등록된 상태인 경우
                if (data.rsltErrCode === "0" && data.rsltYn === "Y") {
                    $('#failModal').css("aria-hidden", "true");
                    $('#exampleModal').css("aria-hidden", "false");

                    $('.inquiryCheckModal').html(``);

                    let html = ``;

                    html += `
                        <form role="form" method="post" enctype="application/x-www-form-urlencoded" action="/page/kb/inquiryHistory.ux">
                            <input type="hidden" name="resultText" value="`+ resultText +`">
                            <div class="modal-content">
                                <div class='modal-header'>
                                    <p>팝업의 타이틀</p>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    1:1 문의가 접수되었습니다.
                                </div>
                
                                <!-- 문의 등록 후 페이지 이동 버튼 -->
                                <div class="modal-footer d-flex justify-content-center align-items-center">
                                    <button type="submit" class="btn btn-primary col-12" data-bs-dismiss="modal" value="inquiryHistory">확인
                                    </button>
                                </div>
                            </div>
                        </form>
                    `;

                    $('.inquiryCheckModal').append(html);

                } else { // 정상적으로 문의가 등록되지 않은 경우
                    // 오류 메세지 알림
                    alert(data.rsltMsge);
                }
            });

    }else{ // 문의 내용이 정상적이지 않을 경우 동작 처리
        $('.inquiryCheckModal').html(``);

        let html = ``;

        html += `
            <div class="modal-content">
                <div class='modal-header'>
                    <p>팝업의 타이틀</p>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    작성 문의 내용을 확인해주십시오.
                </div>
    
                <!--  -->
                <div class="modal-footer d-flex justify-content-center align-items-center">
                    <button type="button" class="btn btn-primary col-12" data-bs-dismiss="modal">확인
                    </button>
                </div>
            </div>
        `;

        $('.inquiryCheckModal').append(html);
    }
}

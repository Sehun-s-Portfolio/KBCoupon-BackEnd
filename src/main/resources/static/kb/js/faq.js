
// FAQ 리스트 호출
function getFaqList(){
    const memberId = document.getElementById('memberId').value;

    // 노출될 FAQ 리스트 호출 api
    fetch("/faq/getFaqList.do?memberId=" + memberId, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then((data) => {
            // 반환받은 FAQ 리스트
            faqData = data.rspList;

            // FAQ 리스트 초기화
            $('.faqList').html(``);

            // 반환받은 FAQ 리스트를 하나씩 HTML 코드로 변환 후 추가
            faqData.forEach(faq => {
                let html = '';

                // 각 FAQ 아이템 HTML
                html += `    
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="flush-headingOne">
                            <button onclick="getFaq(${faq.faqSeq})" class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseOne${faq.faqSeq}" aria-expanded="false" aria-controls="flush-collapseOne${faq.faqSeq}">
                                <span>${faq.type}</span>
                                <p>${faq.faqTitle}</p>
                            </button>
                        </h2>
                        <div id="flush-collapseOne${faq.faqSeq}" class="accordion-collapse collapse faq${faq.faqSeq}" aria-labelledby="flush-headingOne" data-bs-parent="#accordionFlushExample">
                        </div>
                    </div>
                `

                // FAQ 리스트 출력
                $('.faqList').append(html);
            });

        });
}


// 선택한 FAQ의 상세 내용 출력
function getFaq(faqSeq) {

    // 특정 FAQ 호출 api
    fetch("/faq/getFaq.do?faqSeq=" + faqSeq, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then((data) => {
            // 선택한 FAQ 내용
            faqData = data.rspObj;

            var showFaqContentId = "text-flush-collapseOne" + faqSeq;
            document.getElementById(showFaqContentId).innerText = faqData.faqContent;
        });
}






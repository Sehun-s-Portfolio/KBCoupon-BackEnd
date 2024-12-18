$(document).ready(function () {
    // Add click event listeners to the nav links
    document.querySelectorAll('.nav-link').forEach(function (link) {
        link.addEventListener('click', function (event) {
            event.preventDefault(); // Prevent default link behavior

            // Remove 'active' class from all links
            document.querySelectorAll('.nav-link').forEach(function (link) {
                link.classList.remove('active');
            });

            // Add 'active' class to the clicked link
            this.classList.add('active');

            // Get the data-period attribute to determine the filter period
            const period = this.getAttribute('data-period');

            // Call the filter function with the selected period
            filterData(period);
        });
    });
});

// 조회할 월
var fixMonth = 1;

// 자신이 작성한 문의 이력 리스트 호출
function getInquiryList(month, page) {
    // 조회할 월 변경
    fixMonth = month;

    // 문의 리스트가 노출될 최상단 div 초기화
    $('.viewDiv').html(``);

    // 멤버쉽 코드
    const resultText = document.getElementById("resultText").value;
    // 고객사 ID
    const memberId = document.getElementById("memberId").value;

    // 노출될 작성 문의 리스트 호출 api
    fetch("/inquiry/getInquiryList.do", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            resultText: resultText,
            month: fixMonth,
            page: page,
            memberId: memberId
        })
    })
        .then(response => response.json())
        .then((data) => {
            // 반환받은 문의 리스트
            inquiryListData = data.rspList;
            // 반환받은 작성 문의 갯수
            inquiryCountData = data.rspObj;

            // 문의 리스트가 존재하지 않을 경우
            if (inquiryListData.length === 0) {
                let html = '';

                html += `
                    <div class='noHistory col-12 d-flex flex-column align-items-center'>
                        <img src="/kb/images/icon.svg" alt="">
                        <p class="fw-300">조회된 내용이 없습니다.</p>
                    </div>
                `;

                $('.viewDiv').append(html);

            } else if (inquiryListData.length > 0) { // 문의 리스트가 하나라도 존재할 경우
                let html = '<div class="accordion inquiryHistory" id="accordionExample">';

                // 문의 리스트 아이템 하나당 html 코드 변환
                inquiryListData.forEach(inquiry => {

                    // 답변 완료된 데이터인 경우
                    if (inquiry.answerStatus === "Y") {

                        // 답변 완료 문의 영역
                        html += `<div class="accordion-item reply">`;

                        // 문의 사항 영역
                        html += `
                            <h2 class="accordion-header" id="heading${inquiry.inquirySeq}">
                                <button class="accordion-button collapsed" onclick="getInquiryDetail(${inquiry.inquirySeq})" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse${inquiry.inquirySeq}" aria-expanded="true" aria-controls="collapse${inquiry.inquirySeq}">
                                    <div class="col-9">
                                        <div class='mb-1 d-flex justify-content-start gap-3  align-items-center'>
                                            <h6 class='col-12'>
                                                <p class="mb-0 fw-300">[${inquiry.type}] ${inquiry.inquiryTitle}</p>
                                            </h6>
                                        </div>
                                        <div class='mb-2 d-flex justify-content-start gap-3 align-items-center'>
                                            <label class="fw-300">답변 완료</label> 
                                            <span class="fw-400">${inquiry.answerStatusDate}</span>
                                        </div>
                                    </div>
                                </button>
                            </h2>
                        `;

                        // 문의 사항 답변 영역
                        html += `
                                <div id="collapse${inquiry.inquirySeq}" class="accordion-collapse collapse" aria-labelledby="heading${inquiry.inquirySeq}"
                                     data-bs-parent="#accordionExample">
                                    <div class="accordion-body">
                                        <div class='questionDiv questionDiv${inquiry.inquirySeq}' id='inquiryQuestion${inquiry.inquirySeq}' >
                                            
                                        </div>
                                        <div class='replyDiv replyDiv${inquiry.inquirySeq}' id='inquiryReply${inquiry.inquirySeq}' >
                                            
                                        </div>
                                    </div>
                                </div>
                            </div>
                        `;

                    } else { // 답변이 존재하지 않은 경우
                        // 미답변 문의 영역
                        html += `<div class="accordion-item noreply">`;

                        // 문의 사항 영역
                        html += `
                            <h2 class="accordion-header" id="heading${inquiry.inquirySeq}">
                                <button class="accordion-button collapsed" onclick="getInquiryDetail(${inquiry.inquirySeq})" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse${inquiry.inquirySeq}" aria-expanded="true" aria-controls="collapse${inquiry.inquirySeq}">
                                    <div class="col-9">
                                        <div class='mb-1 d-flex justify-content-start gap-3  align-items-center'>
                                            <h6 class='col-12'>
                                                <p class="mb-0 fw-300">[${inquiry.type}] ${inquiry.inquiryTitle}</p>
                                            </h6>
                                        </div>
                                        <div class='mb-2 d-flex justify-content-start gap-3 align-items-center'>
                                            <label class="fw-300">답변 준비중</label> 
                                            <span class="fw-400">${inquiry.answerStatusDate}</span>
                                        </div>
                                    </div>
                                </button>
                            </h2>
                        `;

                        // 미답변 영역
                        html += `
                                <div id="collapse${inquiry.inquirySeq}" class="accordion-collapse collapse" aria-labelledby="heading${inquiry.inquirySeq}"
                                     data-bs-parent="#accordionExample">
                                    <div class="accordion-body">
                                        <div class='questionDiv questionDiv${inquiry.inquirySeq}' id='inquiryQuestion${inquiry.inquirySeq}'>
                                            
                                        </div>
                                        <div class='replyDiv${inquiry.inquirySeq}' id='inquiryReply${inquiry.inquirySeq}'>
                                            
                                        </div>
                                    </div>
                                </div>
                            </div>
                        `;
                    }
                });

                html += `</div>`;

                html += `
                    <nav class='navigation' aria-label="Page navigation example">
                        <ul class="pagination justify-content-center">
                `;

                // 이전 페이지 및 가장 첫번째 페이지 이동
                if (inquiryCountData > 10 && inquiryCountData % 10 === 0) { // 문의 사항 갯수가 10 단위로 떨어질 경우
                    html += `
                        <li class="page-item">
                            <a class="page-link" href="#" aria-label="First" onclick="prevListPage()">
                                <i class="fa-regular fa-chevrons-left"></i>
                            </a>
                        </li>
                        <li class="page-item">
                            <a class="page-link" href="#" aria-label="Previous" onclick="prevPage('case1')">
                                <i class="fa-regular fa-chevron-left"></i>
                            </a>
                        </li>
                    `;
                } else if (inquiryCountData > 10 && inquiryCountData % 10 !== 0) { // 문의 사항 갯수가 10 단위로 떨어지지 않을 경우
                    html += `
                        <li class="page-item">
                            <a class="page-link" href="#" aria-label="First" onclick="prevListPage()">
                                <i class="fa-regular fa-chevrons-left"></i>
                            </a>
                        </li>
                        <li class="page-item">
                            <a class="page-link" href="#" aria-label="Previous" onclick="prevPage('case2')">
                                <i class="fa-regular fa-chevron-left"></i>
                            </a>
                        </li>
                    `;
                }

                // 데이터가 10개 이하인 경우 1페이지 노출
                // if (inquiryCountData <= 10) {
                //     html += `
                //         <li class="page-item disabled">
                //             <span class="page-link fw-bold" id="presentPage" value="1">1</span>
                //             <span class="page-link fw-bold">/</span>
                //             <span class="page-link fw-bold">1</span>
                //         </li>
                //     `;
                // } else
                if (inquiryCountData > 10 && inquiryCountData % 10 === 0) { // 데이터가 10개 이상 10단위로 떨어질 경우 (예 : 데이터 20개면, 총 2페이지)
                    html += `
                        <li class="page-item disabled">
                            <span class="page-link fw-bold" id="presentPage" value="1">` + page + `</span>
                            <span class="page-link">/</span>
                            <span class="page-link" id="maxPage">`;

                    html += Math.floor(inquiryCountData / 10);

                    html += `</span>
                        </li>`;
                } else if (inquiryCountData > 10 && inquiryCountData % 10 !== 0) { // 데이터가 10개 이상 10단위로 떨어지지 않을 경우 (예 : 데이터 13개면, 총 2페이지)
                    html += `
                        <li class="page-item disabled">
                            <span class="page-link fw-bold" id="presentPage2" value="1">` + page + `</span>
                            <span class="page-link">/</span>
                            <span class="page-link" id="maxPage2">`;

                    html += Math.floor(inquiryCountData / 10 + 1);

                    html += `</span>
                        </li>`;
                }

                // 다음 페이지 및 마지막 페이지 이동
                if (inquiryCountData > 10 && inquiryCountData % 10 === 0) { // 문의 사항 갯수가 10 단위로 떨어질 경우
                    html += `
                        <li class="page-item" id="nextArrow">
                            <a class="page-link" href="#" aria-label="Next" onclick="nextPage('case1')">
                                <i class="fa-regular fa-chevron-right"></i>
                            </a>
                        </li>
                        <li class="page-item" id="nextMaxArrow">
                            <a class="page-link" href="#" aria-label="Last" onclick="nextMaxPage('case1')">
                                <i class="fa-regular fa-chevrons-right"></i>
                            </a>
                        </li>
                    `;
                } else if (inquiryCountData > 10 && inquiryCountData % 10 !== 0) { // 문의 사항 갯수가 10 단위로 떨어지지 않을 경우
                    html += `
                        <li class="page-item" id="nextArrow2">
                            <a class="page-link" href="#" aria-label="Next" onclick="nextPage('case2')">
                                <i class="fa-regular fa-chevron-right"></i>
                            </a>
                        </li>
                        <li class="page-item" id="nextMaxArrow2">
                            <a class="page-link" href="#" aria-label="Last" onclick="nextMaxPage('case2')">
                                <i class="fa-regular fa-chevrons-right"></i>
                            </a>
                        </li>
                    `;
                }

                html += `
                        </ul>
                    </nav>
                `;

                $('.viewDiv').append(html);
            }
        });
}


// 문의 내역 상세 조회
function getInquiryDetail(inquirySeq) {

    const memberId = document.getElementById('memberId').value;

    // 노출될 상세 문의 내용 호출 api
    fetch("/inquiry/getInquiryDetail.do?inquirySeq=" + inquirySeq + "&memberId=" + memberId, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then((data) => {
            // 반환받은 상세 문의 내용
            inquiryData = data.rspObj;

            // 특정 문의 영역에 내용 삽입
            var showInquiryQuestion = "inquiryQuestion" + inquirySeq;
            document.getElementById(showInquiryQuestion).innerText = inquiryData.inquiryContent;

            // 답변 완료일 경우에만 진입
            if (inquiryData.answerStatus === "Y") {
                // 특정 문의 영역에 답변 내용 삽입
                var showInquiryReply = "inquiryReply" + inquirySeq;
                document.getElementById(showInquiryReply).innerText = inquiryData.inquiryAnswer;
            }
        });

}


// 이전 페이지 이동
function prevPage(pageCase) {

    // 초기 페이지
    var page = 1;

    // 이동 페이지
    if (pageCase === "case1") { // 문의 갯수가 10단위로 떨어질 경우
        // 현재 페이지 번호
        var presentPage = Number(document.getElementById("presentPage").textContent);

        // 현재 페이지가 1이 아닐 경우
        if (presentPage !== 1) {
            // 이전으로 넘어갈 페이지 번호
            page = presentPage - 1;

            // 이동한 페이지 번호와 현재 선택된 개월 데이터를 가지고 문의 리스트 호출
            getInquiryList(fixMonth, page);
        }
    } else { // 문의 갯수가 10단위로 떨어지지 않을 경우
        // 현재 페이지 번호
        var presentPage2 = Number(document.getElementById("presentPage2").textContent);

        // 현재 페이지가 1이 아닐 경우
        if (presentPage2 !== 1) {
            // 이전으로 넘어갈 페이지 번호
            page = presentPage2 - 1;

            // 이동한 페이지 번호와 현재 선택된 개월 데이터를 가지고 문의 리스트 호출
            getInquiryList(fixMonth, page);
        }
    }
}


// 이전 최소 페이지 이동
function prevListPage() {
    // 최소 페이지
    var page = 1;

    // 이동한 페이지 번호와 현재 선택된 개월 데이터를 가지고 문의 리스트 호출
    getInquiryList(fixMonth, page);
}


// 이후 페이지 이동
function nextPage(pageCase) {
    // 초기 페이지
    var page = 1;

    // 이동 페이지
    if (pageCase === "case1") { // 문의 갯수가 10단위로 떨어질 경우

        // 현재 페이지 번호
        presentPage = Number(document.getElementById("presentPage").textContent);
        // 최대 페이지 번호
        maxPage = Number(document.getElementById("maxPage").textContent);

        // 현재 페이지 번호와 최대 페이지 번호가 일치하지 않을 경우 페이지 이동 허용
        if (presentPage !== maxPage) {
            // 넘어갈 다음 페이지 번호
            page = presentPage + 1;

            // 이동한 페이지 번호와 현재 선택된 개월 데이터를 가지고 문의 리스트 호출
            getInquiryList(fixMonth, page);
        }
    } else { // 문의 갯수가 10단위로 떨어지지 않을 경우

        // 현재 페이지 번호
        presentPage = Number(document.getElementById("presentPage2").textContent);
        // 최대 페이지 번호
        maxPage = Number(document.getElementById("maxPage2").textContent);

        // 현재 페이지 번호와 최대 페이지 번호가 일치하지 않을 경우 페이지 이동 허용
        if (presentPage !== maxPage) {
            // 넘어갈 다음 페이지 번호
            page = presentPage + 1;

            // 이동한 페이지 번호와 현재 선택된 개월 데이터를 가지고 문의 리스트 호출
            getInquiryList(fixMonth, page);
        }
    }

}


// 이후 최대 페이지 이동
function nextMaxPage(pageCase) {
    // 최대 페이지 지정 변수
    var page;

    // 이동 페이지
    if (pageCase === "case1") { // 문의 사항 갯수가 10단위로 떨어질 경우
        page = Number(document.getElementById("maxPage").textContent);
    } else { // 문의 사항 갯수가 10단위로 떨어지지 않을 경우
        page = Number(document.getElementById("maxPage2").textContent);
    }

    // 이동한 페이지 번호와 현재 선택된 개월 데이터를 가지고 문의 리스트 호출
    getInquiryList(fixMonth, page);
}


document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll('.accordion-item.noreply').forEach(function (item) {
        const replyDiv = item.querySelector('.replyDiv');
        if (replyDiv) {
            replyDiv.style.display = 'none'; // replyDiv 숨기기
        }
    });
});


// Get today's date
const today = new Date();

// Convert a string like '2024-12-12' into a Date object
function parseDate(dateString) {
    return new Date(dateString);
}

// Function to subtract months from the current date
function subtractMonths(date, months) {
    const newDate = new Date(date);
    newDate.setMonth(newDate.getMonth() - months);
    return newDate;
}

// Function to filter data based on the clicked period (3, 6, 12 months, or 'all')
function filterData(period) {
    const accordionItems = document.querySelectorAll('.accordion-item');

    // Determine the cutoff date based on the period
    let cutoffDate = null;
    if (period !== 'all') {
        cutoffDate = subtractMonths(today, parseInt(period));
    }

    accordionItems.forEach(function (item) {
        const inquiryDateText = item.querySelector('.inquiry-date').textContent;
        const inquiryDate = parseDate(inquiryDateText.split(" ")[0]); // Extract 'yyyy-mm-dd'

        // Show or hide the item based on the date comparison
        if (period === 'all' || inquiryDate >= cutoffDate) {
            item.style.display = ''; // Show the item
        } else {
            item.style.display = 'none'; // Hide the item
        }
    });
}


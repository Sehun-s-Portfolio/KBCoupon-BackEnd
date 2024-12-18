$(document).ready(function () {
    const allHistoryCoupon = document.querySelectorAll('.newEx');

    // 사용 이력 쿠폰 히스토리가 하나도 없을 경우
    if(allHistoryCoupon.length === 0){
        $('.couponBox')
            .html(``)
            .append(`
            <div class='noneCoupon col-12 d-flex flex-column align-items-center'>
                <img src="https://smilecon-img.zlgoon.com/stardream/kb/images/icon.svg" alt="">
                <p class="fw-300">
                    최근 3개월 내<br>
                    쿠폰 사용 내역이 없습니다.
                </p>
            </div>
        `);
    }else{ // 사용 이력 쿠폰 히스토리가 하나라도 존재할 경우
        const historyCouponList = document.querySelectorAll('.newEx .historyBtn');

        // 히스토리 쿠폰 데이터들 동작 처리
        historyCouponList.forEach((a, index) => {
            a.addEventListener("click", function (e) {
                // 쿠폰 선택 페이지 중복 진입 이벤트 블락 처리
                if(allHistoryCoupon[index].getAttribute("disabled") === true){
                    return;
                }

                allHistoryCoupon[index].setAttribute("disabled", true);

                document.getElementById(a.getAttribute("detailId")).submit();
            })
        })
    }
});

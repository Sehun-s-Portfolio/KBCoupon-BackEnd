$(document).ready(function () {
    const allCoupon = document.querySelectorAll('.newEx');

    // 사용 가능 쿠폰이 하나도 없을 경우
    if (allCoupon.length === 0) {
        $('.couponBox')
            .html(``)
            .append(`
                <div class='noneCoupon col-12 d-flex flex-column align-items-center'>
                    <img src="https://smilecon-img.zlgoon.com/stardream/kb/images/icon.svg" alt="">
                    <p class="fw-300">사용 가능한 쿠폰이 없습니다.</p>
                </div>
        `);
    } else { // 사용 가능한 쿠폰이 하나라도 존재할 경우

        allCoupon.forEach((eachCoupon, index) => {
            if (eachCoupon.querySelector('.status').value !== '0') {
                eachCoupon.classList.remove('active'); // 발급 전 쿠폰의 경우 active class 추가
            }

            // 미발급 쿠폰 + 상세 진입 쿠폰들에게 각각 동작 처리 로직 
            eachCoupon.addEventListener("click", function (e) {
                // 쿠폰 선택 페이지 중복 진입 이벤트 블락 처리
                if (eachCoupon.getAttribute("disabled") === true) {
                    return;
                }

                // 쿠폰 비활성화 처리
                eachCoupon.setAttribute("disabled", true);
                
                // 만약 미발급 쿠폰인 경우
                if(eachCoupon.querySelector('.select') !== null && eachCoupon.querySelector('.detail') === null){
                    document.getElementById(eachCoupon.querySelector('.select').getAttribute("selectCouponId")).submit();
                }else if(eachCoupon.querySelector('.select') === null && eachCoupon.querySelector('.detail') !== null){ // 만약 상세 페이지 진입 쿠폰인 경우
                    document.getElementById(eachCoupon.querySelector('.detail').getAttribute("detailId")).submit();
                }

            })
        })
    }
})
$(document).ready(function () {

    // 페이지 초기 기본 값으로 설정된 굿즈 element
    const defaultGoods = document.querySelector('.Product.couponProduct.Y');
    // 기본 값 설정 굿즈 쿠폰 활성화
    defaultGoods.classList.add('active');

    // 기본 설정된 굿즈의 브랜드 명과 굿즈 명을 통한 쿠폰 받기 모달 내용 초기 설정
    const brandName = defaultGoods.querySelector('.brandName').textContent
    const goodsName = defaultGoods.querySelector('.goodsName').textContent
    selectGoods(brandName, goodsName);

    // 모든 선택 쿠폰 제품 영역
    const goods = document.querySelectorAll('.Product.couponProduct');

    // 선택 쿠폰 라디오 버튼
    const radioButtons = document.querySelectorAll('input[name="selectCoupon"]');

    // 쿠폰 제품 select 리스트 처리
    radioButtons.forEach((radio, index) => {
        // 기본 설정된 쿠폰을 초기 check
        if (radio.className === 'couponActive-Y') {
            radio.checked = true;

            // 설정된 굿즈 제품 설정 색상
            let selectGoodsBorderColor = goods[index].querySelector('.activeCouponColor').value;

            // 쿠폰 선택 화면 진입 시 초기에 활성화된 쿠폰 제품의 border 색상 적용
            goods[index].style.borderColor = selectGoodsBorderColor;

            // Radio Label::Before 스타일 기존 존재 유무 확인
            if(document.getElementById('selectGoodsRadioBefore')){
                // 미리 존재하면 삭제 처리
                document.getElementById('selectGoodsRadioBefore').remove();
            }

            // Radio Label::Before 스타일 구조체 생성
            const beforeStyle = document.createElement('style');
            // Radio Label::Before id 부여
            beforeStyle.id = 'selectGoodsRadioBefore';

            // Radio Label::Before 스타일 생성
            beforeStyle.textContent =
                `.viewDiv .Product div input[type="radio"]:checked + label::before {
                        content: "";
                        border-color:` + selectGoodsBorderColor + `;
                }`;

            // 스타일 반영
            document.head.appendChild(beforeStyle);

            // Radio Label::After 스타일 기존 존재 유무 확인
            if(document.getElementById('selectGoodsRadioAfter')){
                // 미리 존재하면 삭제 처리
                document.getElementById('selectGoodsRadioAfter').remove();
            }

            // Radio Label::After 스타일 구조체 생성
            const afterStyle = document.createElement('style');
            // Radio Label::After id 부여
            afterStyle.id = 'selectGoodsRadioAfter';

            // Radio Label::After 스타일 생성
            afterStyle.textContent =
                `.viewDiv .Product div input[type="radio"]:checked + label::after {
                    content: '';
                    position: absolute;
                    left: 0.31rem;
                    top: 0.31rem;
                    width: 0.625rem;
                    height: 0.625rem;
                    border-radius: 50%;
                    border-color:` + selectGoodsBorderColor + `;
                    background:` +  selectGoodsBorderColor + `;
                }`;

            // 스타일 반영
            document.head.appendChild(afterStyle);

            goodsId = goods[index].querySelector('.goodsId').value;
            goodsSeq = goods[index].querySelector('.goodsSeq').value;
        }

        // 쿠폰 선택 시 동작 처리
        radio.addEventListener('change', () => {

            // 쿠폰 선택 변경 시 활성화 상태인 쿠폰의 class 를 삭제하고 N 속성 추가
            goods.forEach((product) => {
                product.classList.remove('active', 'Y'); // 모든 Product에서 active 클래스 제거

                // Radio Label::Before 스타일 기존 존재 유무 확인
                if(document.getElementById('selectGoodsRadioBefore')){
                    // 미리 존재 시 삭제 처리
                    document.getElementById('selectGoodsRadioBefore').remove();
                }

                // Radio Label::After 스타일 기존 존재 유무 확인
                if(document.getElementById('selectGoodsRadioAfter')){
                    // 미리 존재 시 삭제 처리
                    document.getElementById('selectGoodsRadioAfter').remove();
                }

                // Radio Label::Before 스타일 구조체 생성
                const beforeStyle = document.createElement('style');
                // id 부여
                beforeStyle.id = 'selectGoodsRadioBefore';

                // Radio Label::After 스타일 구조체 생성
                const afterStyle = document.createElement('style');
                // id 부여
                afterStyle.id = 'selectGoodsRadioAfter';

                // 다크 모드일 경우 선택하지 않은 제품들 border 색상 처리
                if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
                    product.style.borderColor = 'var(--kb_UI_04)'; // 선택하지 않은 나머지 쿠폰 제품들의 border 색상을 기본 색상으로 (흰색)

                    // 다크 모드에서 선택되지 않은 Radio Label::Before 기본 스타일 설정
                    beforeStyle.textContent =
                        `.viewDiv .Product div input[type="radio"]:checked + label::before {
                            content: "";
                            border-color: var(--kb_UI_04);
                        }`;

                    // 다크 모드에서 선택되지 않은 Radio Label::After 기본 스타일 설정
                    afterStyle.textContent =
                        `.viewDiv .Product div input[type="radio"]:checked + label::after {
                            content: '';
                            position: absolute;
                            left: 0.31rem;
                            top: 0.31rem;
                            width: 0.625rem;
                            height: 0.625rem;
                            border-radius: 50%;
                            border-color: var(--kb_UI_04);
                            background: var(--kb_UI_04);
                        }`;
                } else { // 화이트 모드일 경우 선택하지 않은 제품들 border 색상 처리
                    product.style.borderColor = 'var(--kb_UI_08)'; // 선택하지 않은 나머지 쿠폰 제품들의 border 색상을 기본 색상으로 (회색?)

                    // 화이트 모드에서 선택되지 않은 Radio Label::Before 기본 스타일 설정
                    beforeStyle.textContent =
                        `.viewDiv .Product div input[type="radio"]:checked + label::before {
                            content: "";
                            border-color: var(--kb_UI_08)
                        }`;

                    // 화이트 모드에서 선택되지 않은 Radio Label::After 기본 스타일 설정
                    afterStyle.textContent =
                        `.viewDiv .Product div input[type="radio"]:checked + label::after {
                            content: '';
                            position: absolute;
                            left: 0.31rem;
                            top: 0.31rem;
                            width: 0.625rem;
                            height: 0.625rem;
                            border-radius: 50%;
                            border-color: var(--kb_UI_08);
                            background: var(--kb_UI_08);
                        }`;
                }

                // 생성한 기본 스타일 반영
                document.head.appendChild(beforeStyle);
                document.head.appendChild(afterStyle);

                product.classList.add('N'); // y 대신 기본 설정 값을 n으로 설정
            });

            // 쿠폰 선택 변경 시 변경된 쿠폰에 대한 동작 처리
            if (radio.checked) {
                let selectGoodsBorderColor = goods[index].querySelector('.activeCouponColor').value;

                goods[index].classList.add('Y', 'active'); // 선택된 radio 인풋의 상위 Product에 active 클래스 추가
                goods[index].style.borderColor = selectGoodsBorderColor; // 선택한 쿠폰의 border 색상을 설정
                goods[index].classList.remove('N'); // 선택한 쿠폰의 n 설정값을 삭제

                // Radio Label::Before 스타일 기존 존재 유무 확인
                if(document.getElementById('selectGoodsRadioBefore')){
                    // 이미 존재하면 삭제 처리
                    document.getElementById('selectGoodsRadioBefore').remove();
                }

                // 새로 적용할 Style 구조체 생성
                const beforeStyle = document.createElement('style');
                // id 값 부여
                beforeStyle.id = 'selectGoodsRadioBefore';

                // Radio Label::Before 스타일 설정
                beforeStyle.textContent =
                    `.viewDiv .Product div input[type="radio"]:checked + label::before {
                        content: "";
                        border-color:` + selectGoodsBorderColor + `;
                    }`;

                // 새로 생성한 스타일 반영
                document.head.appendChild(beforeStyle);

                // Radio Label::After 스타일 기존 존재 유무 확인
                if(document.getElementById('selectGoodsRadioAfter')){
                    // 이미 존재하면 삭제 처리
                    document.getElementById('selectGoodsRadioAfter').remove();
                }

                // 새로 적용할 Style 구조체 생성
                const afterStyle = document.createElement('style');
                // id 값 부여
                afterStyle.id = 'selectGoodsRadioAfter';

                // Radio Label::After 스타일 설정
                afterStyle.textContent =
                    `.viewDiv .Product div input[type="radio"]:checked + label::after {
                    content: '';
                    position: absolute;
                    left: 0.31rem;
                    top: 0.31rem;
                    width: 0.625rem;
                    height: 0.625rem;
                    border-radius: 50%;
                    border-color:` + selectGoodsBorderColor + `;
                    background:` +  selectGoodsBorderColor + `;
                    }`;

                // 새로 생성한 스타일 반영
                document.head.appendChild(afterStyle);

                // 변경한 굿즈 쿠폰의 브랜드 명과 쿠폰 명으로 모달 내용 변경
                const brandName = goods[index].querySelector('.brandName').textContent;
                const goodsName = goods[index].querySelector('.goodsName').textContent;

                selectGoods(brandName, goodsName);

                goodsId = goods[index].querySelector('.goodsId').value;
                goodsSeq = goods[index].querySelector('.goodsSeq').value;
            }
        });
    });


    // 만약 지정된 버튼 색상이 존재할 경우
    if(document.getElementById('btnColor')){
        // 호버된 버튼 스타일 기존 존재 유무 확인
        if(document.getElementById('getCouponButtonHoverStyle')){
            // 미리 존재하면 삭제 처리
            document.getElementById('getCouponButtonHoverStyle').remove();
        }

        // 호버된 버튼에 새롭게 지정할 스타일 element 생성
        const getCouponButtonHoverStyle = document.createElement('style');
        // id 값 부여
        getCouponButtonHoverStyle.id = 'getCouponButtonHoverStyle';

        // 호버된 버튼에 새롭게 지정할 스타일 내용 추가
        getCouponButtonHoverStyle.textContent =
            `.color .btn-primary:hover {
                content: '';
                background-color: ` + document.getElementById('btnColor').value + `!important;
            }`;

        // 스타일 반영
        document.head.appendChild(getCouponButtonHoverStyle);
    }


    // 쿠폰 발급 하기 버튼 동작 후 쿠폰함 이동
    document.getElementById("getCouponButton")
        .addEventListener("click", function (e) {

            // 더블링 이벤트 발생 시 중복을 막기 위한 disabled 처리
            if (this.getAttribute("disabled") === true) {
                return;
            }

            // 쿠폰 발급 버튼 비활성화
            this.setAttribute("disabled", true);

            // 쿠폰 발급 api 호출 실행
            fetch("/page/kb/coupon/getCoupon.do", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    couponInfo: document.getElementById('couponInfo').value,
                    goodsSeq: goodsSeq,
                    goodsId: goodsId,
                })
            })
                .then(response => response.json())
                .then((data) => {
                    this.setAttribute("disabled", false);
                    document.location.href = "/page/kb/availableCoupon.ux?result_text=" + document.getElementById('couponInfo').value;
                })
                .catch((error) => {
                    this.setAttribute("disabled", false);
                    alert("쿠폰 발급에 실패하였습니다.");
                });
        });

})

let goodsId;
let goodsSeq;


// 쿠폰 변경 저장 선택 시 모달 내용 설정
function selectGoods(brandName, goodsName) {
    // 모달에 노출될 선택한 굿트 쿠폰 영역 호출
    const selectGoods = document.querySelector('#selectGoods');

    // 내용 텍스트 구성
    selectGoods.innerHTML = `<p>[` + brandName + `] ` + goodsName + `을 선택하셨습니다.</p>
            <p>선택 완료 후 쿠폰 변경은 불가합니다.</p>`;
}


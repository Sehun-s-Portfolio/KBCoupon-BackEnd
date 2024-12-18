window.onload = function () {

    // 유효 기간 시작 일자 기호 변경
    if(document.getElementById("startDate") != null){
        const getStartDate = document.getElementById("startDate").textContent;
        document.getElementById("startDate").innerText = getStartDate.replaceAll("-", ".");
    }

    // 유효 기간 종료 일자 기호 변경
    const getEndDate = document.getElementById("endDate").textContent;
    document.getElementById("endDate").innerText = getEndDate.replaceAll("-", ".")

    // 쿠폰 캔버스
    const canvas = document.getElementById('canvas');
    const ctx = canvas.getContext('2d');
    const image = new Image();

    image.src = document.getElementById('getCouponImg').value;
    image.crossOrigin = "anonymous";

    // 쿠폰 상세 이미지 캔버스 화
    image.onload = function () {
        canvas.width = image.width;
        canvas.height = image.height;

        // 이미지 캔버스에 그리기
        ctx.drawImage(image, 0, 0);

        // 텍스트 스타일 설정
        ctx.font = "20px Arial";
        ctx.fillStyle = "black";

        // 캔버스 중간에 텍스트 추가
        let expiredEndDateText = document.getElementById("expiredEndDate").value;
        const convertDate = expiredEndDateText.replaceAll("-", ".");
        const checkGoodsId = document.getElementById('checkGoodsId').value;
        const barcodeNumber = document.getElementById('barcodeNumber').value;
        const barcodeCanvas = document.createElement('canvas');

        // 바코드를 사용하는 쿠폰일 경우 쿠폰 이미지에 캔버스 처리
        if (document.getElementById('barcodeStatus').value === 'Y' ||
            document.getElementById('barcodeStatus').value === 'y') {

            // 생성할 바코드 넘버 값
            if (checkGoodsId === '0000094907' || checkGoodsId === '0000095461') {
                ctx.fillText(convertDate + '까지', canvas.width / 3.7, canvas.height - 275);

                // 바코드 생성 후 캔버스화
                JsBarcode(barcodeCanvas, barcodeNumber, {
                    format: "CODE128",     // 바코드 형식 (예: CODE128)
                    width: 2,              // 바코드 막대 너비
                    height: 70,            // 바코드 높이
                    displayValue: true     // 텍스트 표시 여부
                });
            } else {
                ctx.fillText(convertDate, canvas.width / 1.6, canvas.height - 200);

                // 바코드 생성 후 캔버스화
                JsBarcode(barcodeCanvas, barcodeNumber, {
                    format: "CODE128",     // 바코드 형식 (예: CODE128)
                    width: 2,              // 바코드 막대 너비
                    height: 70,            // 바코드 높이
                    displayValue: true     // 텍스트 표시 여부
                });
            }

            const barcodeX = canvas.width / 2 - barcodeCanvas.width / 2;  // X 위치 조정
            const barcodeY = canvas.height - 180;                         // Y 위치 조정

            ctx.drawImage(barcodeCanvas, barcodeX, barcodeY);
        } else {

            if (checkGoodsId === '0000094907' || checkGoodsId === '0000095461') {
                ctx.fillText(convertDate + '까지', canvas.width / 3.7, canvas.height - 275);
            } else {
                ctx.fillText(convertDate, canvas.width / 1.6, canvas.height - 200);
            }

            ctx.fillText(barcodeNumber, canvas.width / 2.7, canvas.height - 120);
        }
    }

    // 쿠폰 유의사항 내용
    const noticeContent = document.getElementById('noticeBox').textContent;
    let firstConvert = noticeContent.replaceAll("\\r\\n", "<br>");
    let secondConvert = firstConvert.replaceAll("\\n\\n", "<br>");
    let thirdConvert = secondConvert.replaceAll("\\n", " ");
    document.getElementById('noticeBox').innerHTML = thirdConvert;

}


// 쿠폰 + 바코드 스크린샷 다운로드
function capture() {
    // 쿠폰 canvas 영역 호출
    const canvas = document.getElementById('canvas');
    // canvas를 png 형식의 데이터로 설정 (base64 기반의 png 암호화 데이터)
    var captureImgData = canvas.toDataURL('image/png');

    // 암호화 png 데이터를 , 기호를 기준으로 절삭 후 실제 사용될 암호화 데이터만 추출하여 atob 함수로 디코딩
    const imgData = atob(captureImgData.split(",")[1]);
    // 디코딩된 png 데이터의 길이 추출
    const len = imgData.length;
    // 추출된 길이를 가지고 buffer 구조의 크기 설정 및 buffer 생성
    const buf = new ArrayBuffer(len); // 비트를 담을 버퍼를 만든다.
    // 만들어진 buffer를 가지고 8bit Unsigned Int 생성
    const view = new Uint8Array(buf); // 버퍼를 8bit Unsigned Int로 담는다.

    let blob, i;

    // 디코딩 데이터 길이에 맞춰 생성된 8bit Unsigned Int에 디코딩된 데이터를 하나씩 저장
    for (i = 0; i < len; i++) {
        view[i] = imgData.charCodeAt(i) & 0xff;
    }

    // 8bit Unsigned Int로 image/png 타입의 Blob 객체를 생성한다. (application/octet-stream도 가능)
    blob = new Blob([view], { type: "image/png" });
    // 생성된 blob 객체가 현재 상주하고 있는 URL 주소 추출
    const url = window.URL.createObjectURL(blob); // blob:http://localhost:1234/28ff8746-94eb-4dbe-9d6c-2443b581dd30

    // 다운로드 할 새로운 a 태그 생성
    var downloadLink = document.createElement('a');

    // 새롭게 생성된 a 태그의 href에 추출한 png 데이터 URL 경로를 설정 및 download 속성 부여, click 함수 실행하여 다운로드 실동작 처리
    downloadLink.href = url;
    downloadLink.download = 'capture_coupon.png';
    downloadLink.click();
    downloadLink.remove();
}
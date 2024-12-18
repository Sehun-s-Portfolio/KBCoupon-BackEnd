package kbcp.site.kb.inquiry.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InquiryType {

    SERVICE("101", "서비스문의"),
    COUPON("102", "쿠폰문의"),
    ETC("103", "기타");

    public String typeCode;
    public String type;

    InquiryType(String typeCode, String type) {
        this.typeCode = typeCode;
        this.type = type;
    }

    public static String getInquiryTypeData(String type){
        switch (type){
            case "서비스문의":
                return SERVICE.typeCode;
            case "쿠폰문의":
                return COUPON.typeCode;
            case "기타":
                return ETC.typeCode;
        }

        return null;
    }


    public static String getInquiryTypeCodeData(String typeCode){
        switch (typeCode){
            case "101":
                return SERVICE.type;
            case "102":
                return COUPON.type;
            case "103":
                return ETC.type;
        }

        return null;
    }
}

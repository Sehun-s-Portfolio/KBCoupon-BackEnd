package kbcp.svc;

import kbcp.common.crypto.CryptoRsa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppCryptoRsa extends CryptoRsa {
    @Value("${app.crypto.private}")
    private String appPrivateKey;

    @Value("${app.crypto.public}")
    private String appPublicKey;

    // RSA 암호화
    public String encode(String plainText) throws Exception {
        return encode(plainText, appPublicKey);
    }

    // RSA 복호화
    public String decode(String decText) throws Exception {
        return decode(decText, appPrivateKey);
    }

}

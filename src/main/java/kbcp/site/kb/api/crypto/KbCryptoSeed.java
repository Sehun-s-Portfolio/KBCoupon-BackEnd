package kbcp.site.kb.api.crypto;

import kbcp.common.crypto.CryptoRsa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class KbCryptoSeed {
    @Value("${site.kb.crypto.seedKey}")
    private String seedKey;

    // 암호화
    public String encode(String encText) throws Exception {
        String result = "";

        // KISA 암호화 로직 시작
        byte[] key = new byte[seedKey.length() / 2];

        for (int i = 0; i < key.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(seedKey.substring(index, index + 2), 16);
            key[i] = (byte) v;
        }
        // KISA-CBC 암호화 기본값 설정
        String charset = "utf-8";
        byte[] msg = null;
        byte[] iv = new byte[16];

        // KISA-CBC 암호화 API실행
        msg = KISA_SEED_CBC.SEED_CBC_Encrypt(key, iv,
                encText.getBytes(charset), 0, encText.getBytes(charset).length);

        // 암호화 (BASE64 인코딩)
        result = new String(Base64.getEncoder().encode(msg));
        return result;
    }

    // 복호화
    public String decode(String decText) throws Exception {
        String result = "";

        // KISA 암호화 로직 시작
        byte[] key = new byte[seedKey.length() / 2];

        for (int i = 0; i < key.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(seedKey.substring(index, index + 2), 16);
            key[i] = (byte) v;
        }
        // KISA-CBC 암호화 기본값 설정
        String charset = "utf-8";
        byte[] iv = new byte[16];

        byte[] decArray = Base64.getDecoder().decode(decText.getBytes(charset));
        result = new String(KISA_SEED_CBC.SEED_CBC_Decrypt(
                key, iv, decArray, 0, decArray.length));

        return result;
    }

}

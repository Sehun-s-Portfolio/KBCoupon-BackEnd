package kbcp.common.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.util.Base64Utils;

@Component
public class CryptoRsa {

    // RSA 암호화
    protected String encode(String plainText, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte [] pByte = Base64.getDecoder().decode(publicKey.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, kf.generatePublic(new X509EncodedKeySpec(pByte)));
        byte[] plainTextByte = cipher.doFinal(plainText.getBytes());
        return Base64Utils.encodeToUrlSafeString(plainTextByte);
//        return Base64.getEncoder().encodeToString(plainTextByte);
    }

    // RSA 복호화
    protected String decode(String decText, String privateKey) throws Exception {
//        byte[] encBodyByte = Base64.getDecoder().decode(decText.getBytes());
        byte[] encBodyByte = Base64Utils.decodeFromUrlSafeString(decText);
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte [] pByte = Base64.getDecoder().decode(privateKey.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, kf.generatePrivate(new PKCS8EncodedKeySpec(pByte)));
        return new String(cipher.doFinal(encBodyByte));
    }

}

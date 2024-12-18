package kbcp.common.crypto;

import kbcp.common.util.LogUtil;
import kbcp.common.util.StringUtil;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class JMessageDigest {
	
	private JMessageDigest() {
		// static 클래스이므로 생성자 사용을 금함.
	};

	// JWT 암호화
	private static String digestSha256(final String salt, final String ctx) {
		String mdString = "";

		if(StringUtil.isNull(ctx)) {
			LogUtil.logError("ctx is empty.");
			return mdString;
		}

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			md.update(salt.getBytes());
			byte[] hashedBytes = md.digest(ctx.getBytes(StandardCharsets.UTF_8));
			mdString = StringUtil.bytesToHex(hashedBytes);
		} catch (Exception e) {
			LogUtil.logException(e);
			return mdString;
		}
		
		return mdString;
	}

	// 기본 salt 암호화
	public static String digest(final String ctx) {
		return digestSha256("2sBreakers^;", ctx);
	}

	// 비밀번호 암호화
	public static String encryptPassword(String id, String password) {
		if(StringUtil.isNull(id)) {
			LogUtil.logError("id is empty.");
			return "";
		}

		if(StringUtil.isNull(password)) {
			LogUtil.logError("password is empty.");
			return "";
		}

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			md.update(id.getBytes());
			byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			return new String(Base64.encodeBase64(hashedBytes));
		} catch (Exception e) {
			LogUtil.logException(e);
			return "";
		}
	}

}

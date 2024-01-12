package vn.com.fpt.jobservice.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class FormatUtil {

	// private static final String HMAC_SHA1 = "HmacSHA1";
	// private static final String SHA1 = "sha1";
	private static final String HMAC_SHA256 = "HmacSHA256";
	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	public static String calcHmacSha256(String key, String data) throws Exception {
		Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256);

		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
		sha256_HMAC.init(secret_key);

		return byteArrayToHex(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
	}

	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}

	public static String getURLWithAppSecret(String baseURL, String secretKey, String accessToken) throws Exception {
		long currentTimestamp = System.currentTimeMillis() / 1000; // new Date().getTime() / 1000;
		return baseURL + "?appsecret_time=" + currentTimestamp + "&appsecret_proof="
				+ calcHmacSha256(secretKey, accessToken + "|" + currentTimestamp);
	}

	/**
	 * Verifies the provided signature of the payload.
	 *
	 * @param payload   the request body {@code JSON payload}
	 * @param signature the SHA1 signature of the request payload
	 * @param appSecret the {@code Application Secret} of the Facebook App
	 * @return {@code true} if the verification was successful, otherwise
	 *         {@code false}
	 */
	public static boolean isSignatureValid(String payload, String signature, String appSecret) {
		try {
			final Mac mac = Mac.getInstance(HMAC_SHA256);
			mac.init(new SecretKeySpec(appSecret.getBytes(), HMAC_SHA256));
			final byte[] rawHmac = mac.doFinal(payload.getBytes());

			final String[] expected = signature.split("=");
			final String actual = bytesToHexString(rawHmac);

			return expected[1].equals(actual);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

	private static String bytesToHexString(byte[] bytes) {
		final char[] hexChars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			final int v = bytes[i] & 0xFF;
			hexChars[i * 2] = HEX_ARRAY[v >>> 4];
			hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
}

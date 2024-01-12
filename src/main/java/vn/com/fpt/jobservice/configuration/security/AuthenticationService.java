package vn.com.fpt.jobservice.configuration.security;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationService {

	// private static final String SIGNINGKEY = "SecretKey";
	private static final String PREFIX = "Bearer";
	private static final String COOKIE_CROWD_NAME = "crowd.token_key";

	public static Authentication getAuthentication(HttpServletRequest req) {

		Enumeration<String> authen = req.getHeaders(HttpHeaders.AUTHORIZATION);
		String token = authen.hasMoreElements() ? authen.nextElement() : null;
		if (StringUtils.isEmpty(token)) {
			token = req.getCookies() == null ? null
					: Arrays.stream(req.getCookies()).filter(x -> COOKIE_CROWD_NAME.equals(x.getName()))
							.map(map -> map.getValue()).collect(Collectors.joining());
		}
		// if (!StringUtils.isEmpty(token)) {
		// try {
		// SecretKey secretKeySpec = Jwts.SIG.HS512.key().build();
		//
		// String user = Jwts.parser().verifyWith(secretKeySpec)
		//// .verifyWith(Keys.hmacShaKeyFor(SIGNINGKEY.getBytes()))
		//// .setSigningKey(secretKeySpec)
		// .build().parseSignedClaims(token.replace(PREFIX, "").trim())
		//// .parseClaimsJws(token.replace(PREFIX, "").trim())
		// .getPayload().getSubject();
		// if (user != null) {
		// return new UsernamePasswordAuthenticationToken(user, null,
		// Collections.emptyList());
		// }
		// } catch (SignatureException e) {
		// log.error("Invalid JWT signature: {}", e.getMessage());
		// } catch (MalformedJwtException e) {
		// log.error("Invalid JWT token: {}", e.getMessage());
		// } catch (ExpiredJwtException e) {
		// log.error("Expired JWT token: {}", e.getMessage());
		// } catch (UnsupportedJwtException e) {
		// log.error("Unsupported JWT token: {}", e.getMessage());
		// } catch (IllegalArgumentException e) {
		// log.error("JWT token compact of handler are invalid: {}", e.getMessage());
		// } catch (Exception e) {
		// log.error("JWT token error: {}", e.getMessage());
		// }
		//
		// }

		return null;
	}

	public static String getTenant(HttpServletRequest req) {
		String tenant = req.getHeader("X-Tenantid");
		return tenant;
		// String token = req.getHeader("Authorization");
		// if (token == null) {
		// return null;
		// }
		// try {
		// SecretKey secretKeySpec = Jwts.SIG.HS512.key().build();
		//// SignatureAlgorithm sa = SignatureAlgorithm.HS512;
		//// SecretKeySpec secretKeySpec = new
		// SecretKeySpec(SIGNINGKEY.getBytes("UTF-8"), sa.getJcaName());
		// String tenant = Jwts.parser().verifyWith(secretKeySpec)
		// // .setSigningKey(SIGNINGKEY)
		// .build().parseSignedClaims(token.replace(PREFIX,
		// "")).getPayload().getAudience().iterator().next();
		// return tenant;
		// } catch (Exception e) {
		// log.error("JWT token error: {}", e.getMessage());
		// }
		// return null;
	}
}

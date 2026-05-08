package com.qinyoucheng.toolkit;

import com.qinyoucheng.toolkit.jwt.JwtProperties;
import com.qinyoucheng.toolkit.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("testSecretKeyForHmacSha256AlgorithmMustBe256BitsLong");
        jwtProperties.setIssuer("test-issuer");
        jwtProperties.setAccessTokenExpiration(3600000L);
        jwtProperties.setRefreshTokenExpiration(86400000L);
        jwtProperties.setAlgorithm(JwtProperties.Algorithm.HMAC);
        jwtUtil = new JwtUtil(jwtProperties);
    }

    @Test
    void testGenerateAndParseAccessToken() {
        String token = jwtUtil.generateAccessToken("user123");
        assertNotNull(token);

        Claims claims = jwtUtil.parseToken(token);
        assertEquals("user123", claims.getSubject());
        assertEquals("test-issuer", claims.getIssuer());
    }

    @Test
    void testGenerateAccessTokenWithClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        claims.put("dept", "tech");

        String token = jwtUtil.generateAccessToken("user123", claims);
        assertNotNull(token);

        Claims parsedClaims = jwtUtil.parseToken(token);
        assertEquals("user123", parsedClaims.getSubject());
        assertEquals("admin", parsedClaims.get("role"));
        assertEquals("tech", parsedClaims.get("dept"));
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtUtil.generateRefreshToken("user123");
        assertNotNull(token);

        Claims claims = jwtUtil.parseToken(token);
        assertEquals("user123", claims.getSubject());
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateAccessToken("user123");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.value"));
    }

    @Test
    void testGetSubject() {
        String token = jwtUtil.generateAccessToken("user123");
        assertEquals("user123", jwtUtil.getSubject(token));
    }

    @Test
    void testIsTokenExpired() {
        JwtProperties shortLivedProps = new JwtProperties();
        shortLivedProps.setSecret("testSecretKeyForHmacSha256AlgorithmMustBe256BitsLong");
        shortLivedProps.setIssuer("test-issuer");
        shortLivedProps.setAccessTokenExpiration(1L);
        shortLivedProps.setAlgorithm(JwtProperties.Algorithm.HMAC);
        JwtUtil shortLivedJwtUtil = new JwtUtil(shortLivedProps);

        String token = shortLivedJwtUtil.generateAccessToken("user123");

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(shortLivedJwtUtil.isTokenExpired(token));
    }

    @Test
    void testRefreshToken() {
        String token = jwtUtil.generateAccessToken("user123");
        String refreshedToken = jwtUtil.refreshToken(token);

        assertNotNull(refreshedToken);
        assertNotEquals(token, refreshedToken);

        Claims claims = jwtUtil.parseToken(refreshedToken);
        assertEquals("user123", claims.getSubject());
    }

    @Test
    void testExtractTokenFromHeader() {
        String header = "Bearer eyJhbGciOiJIUzI1NiJ9.test";
        String token = jwtUtil.extractTokenFromHeader(header);
        assertEquals("eyJhbGciOiJIUzI1NiJ9.test", token);
    }

    @Test
    void testExtractTokenFromInvalidHeader() {
        assertNull(jwtUtil.extractTokenFromHeader("InvalidHeader"));
        assertNull(jwtUtil.extractTokenFromHeader(null));
    }

    @Test
    void testRsaAlgorithm() {
        JwtProperties rsaProps = new JwtProperties();
        rsaProps.setAlgorithm(JwtProperties.Algorithm.RSA);

        try {
            java.security.KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
            String publicKey = java.util.Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = java.util.Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            rsaProps.setRsaPublicKey(publicKey);
            rsaProps.setRsaPrivateKey(privateKey);
        } catch (Exception e) {
            fail("生成RSA密钥对失败: " + e.getMessage());
            return;
        }

        JwtUtil rsaJwtUtil = new JwtUtil(rsaProps);
        String token = rsaJwtUtil.generateAccessToken("userRsa");
        assertNotNull(token);

        Claims claims = rsaJwtUtil.parseToken(token);
        assertEquals("userRsa", claims.getSubject());
    }
}

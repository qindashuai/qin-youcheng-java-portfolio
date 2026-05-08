package com.qindashuai.toolkit.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {

    private final JwtProperties properties;
    private SecretKey hmacKey;
    private PrivateKey rsaPrivateKey;
    private PublicKey rsaPublicKey;

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
        initKeys();
    }

    private void initKeys() {
        if (properties.getAlgorithm() == JwtProperties.Algorithm.HMAC) {
            byte[] keyBytes = properties.getSecret().getBytes();
            if (keyBytes.length < 32) {
                byte[] paddedKey = new byte[32];
                System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
                keyBytes = paddedKey;
            }
            this.hmacKey = Keys.hmacShaKeyFor(keyBytes);
        } else {
            try {
                if (StringUtils.hasText(properties.getRsaPrivateKey())) {
                    byte[] privateKeyBytes = Base64.getDecoder().decode(properties.getRsaPrivateKey());
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                    this.rsaPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
                }
                if (StringUtils.hasText(properties.getRsaPublicKey())) {
                    byte[] publicKeyBytes = Base64.getDecoder().decode(properties.getRsaPublicKey());
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                    this.rsaPublicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
                }
            } catch (Exception e) {
                log.error("初始化RSA密钥失败", e);
                throw new RuntimeException("初始化RSA密钥失败", e);
            }
        }
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, properties.getAccessTokenExpiration());
    }

    public String generateAccessToken(String subject) {
        return generateToken(subject, null, properties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, properties.getRefreshTokenExpiration());
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, null, properties.getRefreshTokenExpiration());
    }

    private String generateToken(String subject, Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuer(properties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate);

        if (claims != null && !claims.isEmpty()) {
            builder.setClaims(claims);
            builder.setSubject(subject);
        }

        if (properties.getAlgorithm() == JwtProperties.Algorithm.HMAC) {
            builder.signWith(hmacKey, SignatureAlgorithm.HS256);
        } else {
            if (rsaPrivateKey == null) {
                throw new IllegalStateException("RSA私钥未配置，无法签发Token");
            }
            builder.signWith(rsaPrivateKey, SignatureAlgorithm.RS256);
        }

        return builder.compact();
    }

    public Claims parseToken(String token) {
        try {
            if (properties.getAlgorithm() == JwtProperties.Algorithm.HMAC) {
                return Jwts.parserBuilder()
                        .setSigningKey(hmacKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } else {
                if (rsaPublicKey == null) {
                    throw new IllegalStateException("RSA公钥未配置，无法验证Token");
                }
                return Jwts.parserBuilder()
                        .setSigningKey(rsaPublicKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            }
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的Token: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("Token签名验证失败: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("Token为空: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubject(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        String subject = claims.getSubject();
        return generateAccessToken(subject);
    }

    public String refreshToken(String token, Map<String, Object> claims) {
        Claims oldClaims = parseToken(token);
        String subject = oldClaims.getSubject();
        if (claims != null) {
            claims.putAll(oldClaims);
            claims.remove("sub");
            claims.remove("iss");
            claims.remove("iat");
            claims.remove("exp");
        }
        return generateAccessToken(subject, claims);
    }

    public long getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    public String extractTokenFromHeader(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(properties.getTokenPrefix())) {
            return authHeader.substring(properties.getTokenPrefix().length());
        }
        return null;
    }
}

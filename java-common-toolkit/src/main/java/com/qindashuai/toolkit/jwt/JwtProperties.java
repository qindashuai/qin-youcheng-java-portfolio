package com.qindashuai.toolkit.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "toolkit.jwt")
public class JwtProperties {

    private String secret = "defaultSecretKeyForHmacSha256AlgorithmMustBe256BitsLong";
    private String issuer = "qindashuai-toolkit";
    private long accessTokenExpiration = 7200000L;
    private long refreshTokenExpiration = 604800000L;
    private String tokenHeader = "Authorization";
    private String tokenPrefix = "Bearer ";
    private Algorithm algorithm = Algorithm.HMAC;

    private String rsaPrivateKey;
    private String rsaPublicKey;

    public enum Algorithm {
        HMAC,
        RSA
    }
}

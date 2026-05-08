package com.qinyoucheng.toolkit.crypto;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class HashUtil {

    private HashUtil() {
    }

    public static String md5(String input) {
        return hash(input, "MD5");
    }

    public static String md5ToBase64(String input) {
        return hashToBase64(input, "MD5");
    }

    public static String sha256(String input) {
        return hash(input, "SHA-256");
    }

    public static String sha256ToBase64(String input) {
        return hashToBase64(input, "SHA-256");
    }

    public static String sha1(String input) {
        return hash(input, "SHA-1");
    }

    public static String sha512(String input) {
        return hash(input, "SHA-512");
    }

    public static String hmacMd5(String input, String key) {
        return hmacHash(input, key, "HmacMD5");
    }

    public static String hmacSha256(String input, String key) {
        return hmacHash(input, key, "HmacSHA256");
    }

    private static String hash(String input, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("哈希算法不可用: {}", algorithm, e);
            throw new RuntimeException("哈希算法不可用: " + algorithm, e);
        }
    }

    private static String hashToBase64(String input, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("哈希算法不可用: {}", algorithm, e);
            throw new RuntimeException("哈希算法不可用: " + algorithm, e);
        }
    }

    private static String hmacHash(String input, String key, String algorithm) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance(algorithm);
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), algorithm);
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            log.error("HMAC哈希失败: {}", algorithm, e);
            throw new RuntimeException("HMAC哈希失败: " + algorithm, e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

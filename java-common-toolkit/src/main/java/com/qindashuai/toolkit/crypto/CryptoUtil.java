package com.qindashuai.toolkit.crypto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoUtil {

    private CryptoUtil() {
    }

    public static String aesEncrypt(String plainText, String base64Key) {
        return AesUtil.encrypt(plainText, base64Key);
    }

    public static String aesDecrypt(String cipherText, String base64Key) {
        return AesUtil.decrypt(cipherText, base64Key);
    }

    public static String aesGenerateKey() {
        return AesUtil.generateKey();
    }

    public static String rsaEncryptByPublicKey(String plainText, String base64PublicKey) {
        return RsaUtil.encryptByPublicKey(plainText, base64PublicKey);
    }

    public static String rsaDecryptByPrivateKey(String cipherText, String base64PrivateKey) {
        return RsaUtil.decryptByPrivateKey(cipherText, base64PrivateKey);
    }

    public static String rsaEncryptByPrivateKey(String plainText, String base64PrivateKey) {
        return RsaUtil.encryptByPrivateKey(plainText, base64PrivateKey);
    }

    public static String rsaDecryptByPublicKey(String cipherText, String base64PublicKey) {
        return RsaUtil.decryptByPublicKey(cipherText, base64PublicKey);
    }

    public static RsaUtil.KeyPairInfo rsaGenerateKeyPair() {
        return RsaUtil.generateKeyPair();
    }

    public static String md5(String input) {
        return HashUtil.md5(input);
    }

    public static String sha256(String input) {
        return HashUtil.sha256(input);
    }

    public static String sha1(String input) {
        return HashUtil.sha1(input);
    }

    public static String sha512(String input) {
        return HashUtil.sha512(input);
    }

    public static String hmacMd5(String input, String key) {
        return HashUtil.hmacMd5(input, key);
    }

    public static String hmacSha256(String input, String key) {
        return HashUtil.hmacSha256(input, key);
    }
}

package com.qinyoucheng.toolkit.crypto;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class RsaUtil {

    private static final String ALGORITHM = "RSA";
    private static final String RSA_ECB_PKCS1 = "RSA/ECB/PKCS1Padding";
    private static final int DEFAULT_KEY_SIZE = 2048;
    private static final int MAX_ENCRYPT_BLOCK_2048 = 245;
    private static final int MAX_DECRYPT_BLOCK_2048 = 256;

    private RsaUtil() {
    }

    public static KeyPairInfo generateKeyPair() {
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }

    public static KeyPairInfo generateKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(keySize, new java.security.SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            return new KeyPairInfo(publicKey, privateKey);
        } catch (Exception e) {
            log.error("生成RSA密钥对失败", e);
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }

    public static String encryptByPublicKey(String plainText, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            PublicKey publicKey = KeyFactory.getInstance(ALGORITHM).generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
            int maxBlock = getMaxEncryptBlock(base64PublicKey);
            return doFinalWithBlock(cipher, data, maxBlock);
        } catch (Exception e) {
            log.error("RSA公钥加密失败", e);
            throw new RuntimeException("RSA公钥加密失败", e);
        }
    }

    public static String decryptByPrivateKey(String cipherText, String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM).generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] data = Base64.getDecoder().decode(cipherText);
            int maxBlock = getMaxDecryptBlock(base64PrivateKey);
            byte[] result = doFinalWithBlockRaw(cipher, data, maxBlock);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RSA私钥解密失败", e);
            throw new RuntimeException("RSA私钥解密失败", e);
        }
    }

    public static String encryptByPrivateKey(String plainText, String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM).generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
            int maxBlock = getMaxEncryptBlock(base64PrivateKey);
            return doFinalWithBlock(cipher, data, maxBlock);
        } catch (Exception e) {
            log.error("RSA私钥加密失败", e);
            throw new RuntimeException("RSA私钥加密失败", e);
        }
    }

    public static String decryptByPublicKey(String cipherText, String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            PublicKey publicKey = KeyFactory.getInstance(ALGORITHM).generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            byte[] data = Base64.getDecoder().decode(cipherText);
            int maxBlock = getMaxDecryptBlock(base64PublicKey);
            byte[] result = doFinalWithBlockRaw(cipher, data, maxBlock);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RSA公钥解密失败", e);
            throw new RuntimeException("RSA公钥解密失败", e);
        }
    }

    private static String doFinalWithBlock(Cipher cipher, byte[] data, int maxBlock) throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int offset = 0;
        while (offset < data.length) {
            int blockLen = Math.min(data.length - offset, maxBlock);
            byte[] block = cipher.doFinal(data, offset, blockLen);
            out.write(block);
            offset += blockLen;
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    private static byte[] doFinalWithBlockRaw(Cipher cipher, byte[] data, int maxBlock) throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int offset = 0;
        while (offset < data.length) {
            int blockLen = Math.min(data.length - offset, maxBlock);
            byte[] block = cipher.doFinal(data, offset, blockLen);
            out.write(block);
            offset += blockLen;
        }
        return out.toByteArray();
    }

    private static int getMaxEncryptBlock(String key) {
        int keySize = getKeySizeFromKey(key);
        return keySize / 8 - 11;
    }

    private static int getMaxDecryptBlock(String key) {
        int keySize = getKeySizeFromKey(key);
        return keySize / 8;
    }

    private static int getKeySizeFromKey(String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        int byteLength = keyBytes.length;
        if (byteLength > 300) return 2048;
        if (byteLength > 150) return 1024;
        return 512;
    }

    public static class KeyPairInfo {
        private final String publicKey;
        private final String privateKey;

        public KeyPairInfo(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() { return publicKey; }
        public String getPrivateKey() { return privateKey; }
    }
}

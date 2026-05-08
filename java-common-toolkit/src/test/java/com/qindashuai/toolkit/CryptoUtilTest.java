package com.qindashuai.toolkit;

import com.qindashuai.toolkit.crypto.AesUtil;
import com.qindashuai.toolkit.crypto.CryptoUtil;
import com.qindashuai.toolkit.crypto.HashUtil;
import com.qindashuai.toolkit.crypto.RsaUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilTest {

    @Test
    void testAesEncryptAndDecrypt() {
        String key = AesUtil.generateKey();
        assertNotNull(key);

        String plainText = "Hello, World! 你好，世界！";
        String cipherText = CryptoUtil.aesEncrypt(plainText, key);
        assertNotNull(cipherText);
        assertNotEquals(plainText, cipherText);

        String decryptedText = CryptoUtil.aesDecrypt(cipherText, key);
        assertEquals(plainText, decryptedText);
    }

    @Test
    void testAesEncryptAndDecryptWithDifferentKeys() {
        String key1 = AesUtil.generateKey();
        String key2 = AesUtil.generateKey();

        String plainText = "Test Data";
        String cipherText = AesUtil.encrypt(plainText, key1);

        assertThrows(RuntimeException.class, () -> AesUtil.decrypt(cipherText, key2));
    }

    @Test
    void testAesEmptyString() {
        String key = AesUtil.generateKey();
        String plainText = "";
        String cipherText = AesUtil.encrypt(plainText, key);
        String decryptedText = AesUtil.decrypt(cipherText, key);
        assertEquals(plainText, decryptedText);
    }

    @Test
    void testRsaEncryptAndDecrypt() {
        RsaUtil.KeyPairInfo keyPair = RsaUtil.generateKeyPair();
        assertNotNull(keyPair.getPublicKey());
        assertNotNull(keyPair.getPrivateKey());

        String plainText = "RSA Test Data";
        String cipherText = CryptoUtil.rsaEncryptByPublicKey(plainText, keyPair.getPublicKey());
        assertNotNull(cipherText);

        String decryptedText = CryptoUtil.rsaDecryptByPrivateKey(cipherText, keyPair.getPrivateKey());
        assertEquals(plainText, decryptedText);
    }

    @Test
    void testRsaPrivateKeyEncryptAndPublicKeyDecrypt() {
        RsaUtil.KeyPairInfo keyPair = RsaUtil.generateKeyPair();

        String plainText = "RSA Private Key Encrypt";
        String cipherText = RsaUtil.encryptByPrivateKey(plainText, keyPair.getPrivateKey());
        assertNotNull(cipherText);

        String decryptedText = RsaUtil.decryptByPublicKey(cipherText, keyPair.getPublicKey());
        assertEquals(plainText, decryptedText);
    }

    @Test
    void testRsaLongText() {
        RsaUtil.KeyPairInfo keyPair = RsaUtil.generateKeyPair();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append("A");
        }
        String longText = sb.toString();

        String cipherText = RsaUtil.encryptByPublicKey(longText, keyPair.getPublicKey());
        String decryptedText = RsaUtil.decryptByPrivateKey(cipherText, keyPair.getPrivateKey());
        assertEquals(longText, decryptedText);
    }

    @Test
    void testMd5() {
        String input = "hello";
        String hash = CryptoUtil.md5(input);
        assertNotNull(hash);
        assertEquals(32, hash.length());
        assertEquals("5d41402abc4b2a76b9719d911017c592", hash);
    }

    @Test
    void testSha256() {
        String input = "hello";
        String hash = CryptoUtil.sha256(input);
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    void testSha1() {
        String input = "hello";
        String hash = CryptoUtil.sha1(input);
        assertNotNull(hash);
        assertEquals(40, hash.length());
    }

    @Test
    void testSha512() {
        String input = "hello";
        String hash = CryptoUtil.sha512(input);
        assertNotNull(hash);
        assertEquals(128, hash.length());
    }

    @Test
    void testHmacMd5() {
        String input = "hello";
        String key = "secretKey";
        String hash = CryptoUtil.hmacMd5(input, key);
        assertNotNull(hash);
        assertEquals(32, hash.length());
    }

    @Test
    void testHmacSha256() {
        String input = "hello";
        String key = "secretKey";
        String hash = CryptoUtil.hmacSha256(input, key);
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    void testMd5Consistency() {
        String input = "testConsistency";
        String hash1 = HashUtil.md5(input);
        String hash2 = HashUtil.md5(input);
        assertEquals(hash1, hash2);
    }

    @Test
    void testMd5DifferentInputs() {
        String hash1 = HashUtil.md5("input1");
        String hash2 = HashUtil.md5("input2");
        assertNotEquals(hash1, hash2);
    }
}

package com.fm.base.crypto;

import org.apache.commons.lang3.RandomUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public abstract class CoreCryptoV1Base implements CoreCrypto {
    private static final int BLOCK_SIZE = 16;
    private static final int SALT_SIZE = 8;
    private static final String DEFAULT_HASH_ALGORITHM = "SHA-256";
    private static final String DEFAULT_ENCRYPT_ALGORITHM = "AES";

    private byte[] password;
    private String fixedSalt;
    private String hashAlgorithm;
    private String encryptAlgorithm;
    protected final static String baseVersion = "V1";
    protected final static String SEPARATOR = "$";

    @Override
    public void init(String secret, String fixedSalt, String hashAlgorithm, String encryptAlgorithm) throws InstantiationError {
        if (secret == null || secret.isEmpty())
            throw new IllegalArgumentException("Missing secret");
        this.password = secret.getBytes(StandardCharsets.UTF_8);
        this.fixedSalt = fixedSalt;
        this.hashAlgorithm = Optional.ofNullable(hashAlgorithm).orElse(DEFAULT_HASH_ALGORITHM);
        this.encryptAlgorithm = Optional.ofNullable(encryptAlgorithm).orElse(DEFAULT_ENCRYPT_ALGORITHM);
    }

    @Override
    public String encryptString(String strToEncrypt) throws GeneralSecurityException, IOException {
        byte[] salt = genSalt();
        byte[] cipherBytes = encrypt(salt, strToEncrypt.getBytes(StandardCharsets.UTF_8));
        return String.format("%s%s%s",
                Base64.getEncoder().encodeToString(salt),
                SEPARATOR,
                Base64.getEncoder().encodeToString(cipherBytes));
    }

    @Override
    public String decryptString(String strToEncrypt) throws GeneralSecurityException, IOException {
        int delim = strToEncrypt.indexOf(SEPARATOR);
        if (delim < 0) {
            throw new IOException("Malformed input string");
        }
        String salt = strToEncrypt.substring(0, delim);
        String cipher = strToEncrypt.substring(delim + 1);
        byte[] cipherBytes = Base64.getDecoder().decode(cipher);
        return new String(decrypt(Base64.getDecoder().decode(salt), cipherBytes));
    }

    @Override
    public byte[] genSalt() {
        return RandomUtils.nextBytes(SALT_SIZE);
    }

    public abstract String getVersion();

    /**
     * A function to generate fixed Salt, which can vary depending on each implementation
     * @return an array of bytes
     */
    abstract byte[] genFixedSalt();

    abstract Cipher prepareCipher(int mode, byte[] salt) throws GeneralSecurityException;

    public byte[] encrypt(byte[] salt, byte[] input) throws GeneralSecurityException {
        Cipher cipher = prepareCipher(Cipher.ENCRYPT_MODE, salt);
        return cipher.doFinal(input);
    }

    public String encryptStringWithFixedSalt(final String input) throws GeneralSecurityException {
        return new String(encrypt(fixedSalt == null ? genFixedSalt() : fixedSalt.getBytes(StandardCharsets.UTF_8),
            input.getBytes()), StandardCharsets.UTF_8);
    }

    public byte[] decrypt(byte[] salt, byte[] input) throws GeneralSecurityException {
        Cipher cipher = prepareCipher(Cipher.DECRYPT_MODE, salt);
        return cipher.doFinal(input);
    }

    protected SecretKeySpec getKey(byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest keyMd = MessageDigest.getInstance(hashAlgorithm);
        keyMd.update(password);
        keyMd.update(genFixedSalt());
        keyMd.update(salt);
        byte[] keyBytes = keyMd.digest();
        return new SecretKeySpec(keyBytes, encryptAlgorithm);
    }

    protected IvParameterSpec getIv(byte[] keyBytes, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest ivMd = MessageDigest.getInstance(hashAlgorithm);
        ivMd.update(keyBytes);
        ivMd.update(salt);
        byte[] ivBytes = Arrays.copyOf(ivMd.digest(), BLOCK_SIZE);
        return new IvParameterSpec(ivBytes);
    }
}

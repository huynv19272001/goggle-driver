package com.fm.base.crypto;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class CoreCryptoV1Impl extends CoreCryptoV1Base {
    private static final String DEFAULT_FIXED_SALT = "Pnze98B998ydeEjrCM8QffXb6z8Q66jEepGNjB7a";
    private static final byte[] DEFAULT_FIXED_SALT_BYTES;

    // static block
    static {
        DEFAULT_FIXED_SALT_BYTES = DEFAULT_FIXED_SALT.getBytes(StandardCharsets.UTF_8);
    }

    private Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance("AES/CBC/PKCS5PADDING");
    }

    @Override
    public Cipher prepareCipher(int mode, byte[] salt) throws GeneralSecurityException {
        Cipher cipher = getCipher();
        SecretKeySpec key = getKey(salt);
        IvParameterSpec iv = getIv(key.getEncoded(), salt);
        cipher.init(mode, key, iv);
        return cipher;
    }

    @Override
    public String getVersion() {
        return baseVersion;
    }

    @Override
    public byte[] genFixedSalt() {
        return DEFAULT_FIXED_SALT_BYTES;
    }
}



package com.fm.base.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface CoreCrypto {
    void init(String secret, String fixedSalt, String hashAlgorithm, String encryptAlgorithm);
    String getVersion();
    byte[] genSalt();
    byte[] encrypt(byte[] salt, byte[] input) throws GeneralSecurityException;
    String encryptStringWithFixedSalt(String input) throws GeneralSecurityException;
    String encryptString(String input) throws GeneralSecurityException, IOException;
    byte[] decrypt(byte[] salt, byte[] input) throws GeneralSecurityException;
    String decryptString(String input) throws GeneralSecurityException, IOException;
}

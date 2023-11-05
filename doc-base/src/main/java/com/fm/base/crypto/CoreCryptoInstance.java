package com.fm.base.crypto;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CoreCryptoInstance {
    public static final String PREFIX = "DIONYSUS_";
    public static final String SEPARATOR = "|";
    private static CoreCrypto instance = null;
    private static Map<String, CoreCrypto> instances;
    private static final Logger LOG = LoggerFactory.getLogger(CoreCryptoInstance.class);

    private CoreCryptoInstance() {
    }

    public static void init(String secret, String fixedSalt, String hashAlgorithm, String encryptAlgorithm) {
        // default version for encrypting
        instance = new CoreCryptoV1Impl();
        instance.init(secret, fixedSalt, hashAlgorithm, encryptAlgorithm);

        instances = new HashMap<>();
        instances.put(instance.getVersion(), instance);
    }

    public static CoreCrypto getInstance() {
        return instance;
    }

    public static String encrypt(String s) {
        if (instance != null && s != null) {
            try {
                String version = getEncryptedVersion(s);
                if (version == null) {
                    return PREFIX + instance.getVersion() + SEPARATOR + instance.encryptString(s);
                }
            } catch (Exception ex) {
                log.error("Error while encrypting data", ex);
            }
        }
        return s;
    }

    public static String encryptWithFixedSalt(final String s) {
        if (instance != null && s != null) {
            try {
                String version = getEncryptedVersion(s);
                if (version == null) {
                    return PREFIX + instance.getVersion() + SEPARATOR + instance.encryptStringWithFixedSalt(s);
                }
            } catch (Exception ex) {
                log.error("Error while encrypting data", ex);
            }
        }
        return s;
    }

    public static CoreCrypto getInstance(String version) {
        return instances.get(version);
    }

    public static String decrypt(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        int delim = s.indexOf(SEPARATOR);
        if (delim < 0) {
            log.error("Wrong input format");
            return s;
        }

        String version = getEncryptedVersion(s);
        if (version == null) {
            log.error("Wrong input format");
            return s;
        }
        CoreCrypto crypto = getInstance(version);
        if (crypto == null)
            return s;

        try {
            return crypto.decryptString(s.substring(delim + 1));
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error while decrypting", e);
        }
        return s;
    }

    public static String getEncryptedVersion(String v) {
        int idx = v.indexOf(SEPARATOR);
        if (idx < 0) return null;
        String prefix = v.substring(0, idx);
        if (!prefix.startsWith(PREFIX)) return null;
        return prefix.substring(PREFIX.length());
    }

}

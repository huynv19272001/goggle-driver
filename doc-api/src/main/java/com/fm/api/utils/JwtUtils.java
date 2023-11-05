//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fm.api.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

public class JwtUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET_NUMBER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_ALPHABET_NUMBER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String UPPER_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public JwtUtils() {
    }

    public static Builder setClaims(Builder builder, Map<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            Iterator var2 = data.entrySet().iterator();

            while(true) {
                while(true) {
                    Entry entry;
                    Object v;
                    do {
                        if (!var2.hasNext()) {
                            return builder;
                        }

                        entry = (Entry)var2.next();
                        v = entry.getValue();
                    } while(v == null);

                    if (v instanceof List && !((List)v).isEmpty()) {
                        Object v1 = ((List)v).get(0);
                        List list;
                        if (v1 instanceof String) {
                            list = (List)v;
                            String[] arr = new String[list.size()];
                            arr = (String[])list.toArray(arr);
                            builder.withArrayClaim((String)entry.getKey(), arr);
                        } else if (v1 instanceof Long) {
                            list = (List)v;
                            Long[] arr = new Long[list.size()];
                            arr = (Long[])list.toArray(arr);
                            builder.withArrayClaim((String)entry.getKey(), arr);
                        } else {
                            if (!(v1 instanceof Integer)) {
                                throw new InvalidClaimException(MessageFormat.format("Invalid array type for {0}, type is: {1}", entry.getKey(), v.getClass()));
                            }

                            list = (List)v;
                            Integer[] arr = new Integer[list.size()];
                            arr = (Integer[])list.toArray(arr);
                            builder.withArrayClaim((String)entry.getKey(), arr);
                        }
                    } else if (v instanceof String) {
                        builder.withClaim((String)entry.getKey(), (String)v);
                    } else if (v instanceof Integer) {
                        builder.withClaim((String)entry.getKey(), (Integer)v);
                    } else if (v instanceof Long) {
                        builder.withClaim((String)entry.getKey(), (Long)v);
                    } else if (v instanceof Double) {
                        builder.withClaim((String)entry.getKey(), (Double)v);
                    } else if (v instanceof Boolean) {
                        builder.withClaim((String)entry.getKey(), (Boolean)v);
                    } else if (v instanceof Date) {
                        builder.withClaim((String)entry.getKey(), (Date)v);
                    } else {
                        if (!(v instanceof DateTime)) {
                            throw new InvalidClaimException(MessageFormat.format("Invalid type for {0}, type is: {1}", entry.getKey(), v.getClass()));
                        }

                        builder.withClaim((String)entry.getKey(), ((DateTime)v).toDate());
                    }
                }
            }
        } else {
            return builder;
        }
    }

    public static String encode(String secretKey, String issuer, Map<String, Object> data) throws InvalidClaimException {
        Algorithm algorithmHS = Algorithm.HMAC256(secretKey);
        Builder builder = JWT.create().withIssuer(issuer);
        builder = setClaims(builder, data);
        return builder.sign(algorithmHS);
    }

    public static String encode(String secretKey, String issuer, JWTEntry jwtEntry) throws InvalidClaimException {
        Algorithm algorithmHS = Algorithm.HMAC256(secretKey);
        Builder builder = JWT.create().withIssuer(issuer);
        if (jwtEntry.getSubject() != null) {
            builder.withSubject(jwtEntry.getSubject());
        }

        if (jwtEntry.getHeaders() != null && !jwtEntry.getHeaders().isEmpty()) {
            builder.withHeader(jwtEntry.getHeaders());
            builder.withHeader(jwtEntry.getHeaders());
        }

        if (jwtEntry.getClaims() != null && !jwtEntry.getClaims().isEmpty()) {
            builder = setClaims(builder, jwtEntry.getClaims());
        }

        return builder.sign(algorithmHS);
    }

    public static DecodedJWT decode(String secretKey, String issuer, String token) {
        Algorithm algorithmHS = Algorithm.HMAC256(secretKey);
        JWTVerifier jwtVerifier = JWT.require(algorithmHS).withIssuer(issuer).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT;
    }

    public static String getSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for(int i = 0; i < length; ++i) {
            returnValue.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(RANDOM.nextInt("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length())));
        }

        return new String(returnValue);
    }

    public static String getUpperSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for(int i = 0; i < length; ++i) {
            returnValue.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(RANDOM.nextInt("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".length())));
        }

        return new String(returnValue);
    }

    public static String getUpperAlphabetSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for(int i = 0; i < length; ++i) {
            returnValue.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(RANDOM.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZ".length())));
        }

        return new String(returnValue);
    }

    public static String md5(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(value.getBytes());
        byte[] digest = md.digest();
//        return DatatypeConverter.printHexBinary(digest);
        return digest.toString();
    }
}

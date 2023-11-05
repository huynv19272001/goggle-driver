package com.fm.base.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fm.base.oauth.JWTEntry;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JwtUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET_NUMBER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_ALPHABET_NUMBER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String UPPER_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";



    public static JWTCreator.Builder setClaims(JWTCreator.Builder builder, Map<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, Object> entry: data.entrySet()) {
                Object v = entry.getValue();
                if (v == null) {
                    // do nothing now
                }
                else if (v instanceof List && !((List) v).isEmpty()) {
                    Object v1 = ((List) v).get(0);
                    if (v1 instanceof String) {
                        List<String> list = (List<String>) v;
                        String[] arr = new String[list.size()];
                        arr = list.toArray(arr);
                        builder.withArrayClaim(entry.getKey(), arr);
                    } else if (v1 instanceof Long) {
                        List<Long> list = (List<Long>) v;
                        Long[] arr = new Long[list.size()];
                        arr = list.toArray(arr);
                        builder.withArrayClaim(entry.getKey(), arr);
                    } else if (v1 instanceof Integer) {
                        List<Integer> list = (List<Integer>) v;
                        Integer[] arr = new Integer[list.size()];
                        arr = list.toArray(arr);
                        builder.withArrayClaim(entry.getKey(), arr);
                    } else {
                        throw new InvalidClaimException(
                                MessageFormat.format("Invalid array type for {0}, type is: {1}", entry.getKey(), v.getClass())
                        );
                    }
                } else if (v instanceof String) {
                    builder.withClaim(entry.getKey(), (String) v);
                } else if (v instanceof Integer) {
                    builder.withClaim(entry.getKey(), (Integer) v);
                } else if (v instanceof Long) {
                    builder.withClaim(entry.getKey(), (Long) v);
                } else if (v instanceof Double) {
                    builder.withClaim(entry.getKey(), (Double) v);
                } else if (v instanceof Boolean) {
                    builder.withClaim(entry.getKey(), (Boolean) v);
                } else if (v instanceof Date) {
                    builder.withClaim(entry.getKey(), (Date) v);
                } else if (v instanceof DateTime) {
                    builder.withClaim(entry.getKey(), ((DateTime) v).toDate());
                } else {
                    throw new InvalidClaimException(
                            MessageFormat.format("Invalid type for {0}, type is: {1}", entry.getKey(), v.getClass())
                    );
                }
            }
        }
        return builder;
    }

    public static String encode(String secretKey, String issuer, Map<String, Object> data) throws InvalidClaimException{
        Algorithm algorithmHS = Algorithm.HMAC256(secretKey);
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(issuer);

        builder = JwtUtils.setClaims(builder, data);
        return builder.sign(algorithmHS);
    }

    public static String encode(String secretKey, String issuer, JWTEntry jwtEntry) throws InvalidClaimException{
        Algorithm algorithmHS = Algorithm.HMAC256(secretKey);
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(issuer);
        if (jwtEntry.getSubject() != null) {
            builder.withSubject(jwtEntry.getSubject());
        }

        if (jwtEntry.getHeaders() != null && !jwtEntry.getHeaders().isEmpty()) {
            builder.withHeader(jwtEntry.getHeaders());
            builder.withHeader(jwtEntry.getHeaders());
        }

        if (jwtEntry.getClaims() != null && !jwtEntry.getClaims().isEmpty()) {
            builder = JwtUtils.setClaims(builder, jwtEntry.getClaims());
        }

        return builder.sign(algorithmHS);
    }

    public static DecodedJWT decode(String secretKey, String issuer, String token) {
        Algorithm algorithmHS = Algorithm.HMAC256(secretKey);
        JWTVerifier jwtVerifier = JWT.require(algorithmHS)
                .withIssuer(issuer)
                .build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT;
    }

    public static String getSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET_NUMBER.charAt(RANDOM.nextInt(ALPHABET_NUMBER.length())));
        }
        return new String(returnValue);
    }

    public static String getUpperSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(UPPER_ALPHABET_NUMBER.charAt(RANDOM.nextInt(UPPER_ALPHABET_NUMBER.length())));
        }
        return new String(returnValue);
    }

    public static String getUpperAlphabetSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(UPPER_ALPHABET.charAt(RANDOM.nextInt(UPPER_ALPHABET.length())));
        }
        return new String(returnValue);
    }

    public static String md5(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(value.getBytes());
        byte[] digest = md.digest();

        return DatatypeConverter.printHexBinary(digest);
    }



//    public static Optional<String> decode(String key, String token) {
//        try {
////            Algorithm algorithm = Algorithm.HMAC256(key);
////            JWTVerifier verifier = JWT.require(algorithm)
////                    .withIssuer("auth0")
////                    .build(); //Reusable verifier instance
////            DecodedJWT jwt = verifier.verify(token);
////            return Optional.ofNullable(jwt.toString());
//            DecodedJWT decode = JWT.decode(token);
//
//        } catch (JWTVerificationException exception){
//            //Invalid signature/claims
//            return Optional.empty();
//        }
//    }


}

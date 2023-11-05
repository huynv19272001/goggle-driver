package com.fm.base.oauth;

import java.util.HashMap;
import java.util.Map;

public class JWTEntry {
    private String subject;
    private Map<String, Object> headers = new HashMap<>();
    private Map<String, Object> claims = new HashMap<String, Object>();

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public void setClaims(Map<String, Object> claims) {
        this.claims = claims;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void addClaim(String key, Object v) {
        claims.put(key, v);
    }

    public Object getClaim(String key) {
        return claims.get(key);
    }

    public Object getHeader(String key) {
        return headers.get(key);
    }
}

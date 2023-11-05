//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fm.api.utils;

import java.util.HashMap;
import java.util.Map;

public class JWTEntry {
    private String subject;
    private Map<String, Object> headers = new HashMap();
    private Map<String, Object> claims = new HashMap();

    public JWTEntry() {
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return this.subject;
    }

    public Map<String, Object> getClaims() {
        return this.claims;
    }

    public void setClaims(Map<String, Object> claims) {
        this.claims = claims;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    public void addClaim(String key, Object v) {
        this.claims.put(key, v);
    }

    public Object getClaim(String key) {
        return this.claims.get(key);
    }

    public Object getHeader(String key) {
        return this.headers.get(key);
    }
}

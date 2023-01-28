package io.junpring.coupang.entities.member;

import java.util.Date;

public class SessionEntity {
    private Date createdAt; // 로그인한 시간 (Cookie 생성시간)
    private Date updatedAt; // (Cookie)업데이트된 시간
    private Date expiresAt; // (Cookie)만료 시간
    private boolean isExpired; // Cookie 만료된 여부 , 1이면 만료됨. (DEFAULT FALSE)
    private String userEmail; // `coupang_member`.`user` (`email`)를 참조 함.
    private String key; // sessionKey(Hashing), PRIMARY KEY (`key`)
    private String ua; // userAgent(Hashing)


    public SessionEntity () {

    }
    public SessionEntity(Date createdAt, Date updatedAt, Date expiresAt, boolean isExpired, String userEmail, String key, String ua) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.isExpired = isExpired;
        this.userEmail = userEmail;
        this.key = key;
        this.ua = ua;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }
}

package io.junpring.coupang.vos.member.user;

import io.junpring.coupang.entities.member.SessionEntity;
import io.junpring.coupang.entities.member.UserEntity;
import io.junpring.coupang.enums.member.user.LoginResult;

public class LoginVo extends UserEntity  {
    private LoginResult result;
    private boolean autosign;
    private SessionEntity sessionEntity; // SessionEntity 타입 (확장?)

    public LoginResult getResult() {
        return result;
    }

    public void setResult(LoginResult result) {
        this.result = result;
    }

    public boolean isAutosign() {
        return autosign;
    }

    public void setAutosign(boolean autosign) {
        this.autosign = autosign;
    }

    public SessionEntity getSessionEntity() {
        return sessionEntity;
    }

    public void setSessionEntity(SessionEntity sessionEntity) {
        this.sessionEntity = sessionEntity;
    }
}

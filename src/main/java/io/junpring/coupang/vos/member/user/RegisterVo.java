package io.junpring.coupang.vos.member.user;

import io.junpring.coupang.entities.member.UserEntity;
import io.junpring.coupang.enums.member.user.RegisterResult;


public class RegisterVo extends UserEntity  {
    RegisterResult result;

    public RegisterResult getResult() {
        return result;
    }

    public void setResult(RegisterResult result) {
        this.result = result;
    }
}


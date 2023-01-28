package io.junpring.coupang.mappers;

import io.junpring.coupang.entities.member.SessionEntity;
import io.junpring.coupang.entities.member.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IUserMapper {
    int insertSession(SessionEntity sessionEntity);
    int insertUser(UserEntity userEntity);

    UserEntity selectUser(UserEntity userEntity);

    UserEntity selectUserByEmail(
            @Param(value = "email")String email);

    int selectUserCountBy(
            @Param(value = "email")String email);

    SessionEntity selectSessionByKey(
            @Param(value = "key") String key);

    int updateSession(SessionEntity sessionEntity);
    int updateSessionExpiredByEmail(
            @Param(value = "email") String email);
}

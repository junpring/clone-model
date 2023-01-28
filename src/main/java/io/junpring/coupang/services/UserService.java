package io.junpring.coupang.services;

import io.junpring.coupang.entities.member.SessionEntity;
import io.junpring.coupang.entities.member.UserEntity;
import io.junpring.coupang.enums.member.user.LoginResult;
import io.junpring.coupang.enums.member.user.RegisterResult;
import io.junpring.coupang.mappers.IUserMapper;
import io.junpring.coupang.utils.CryptoUtil;
import io.junpring.coupang.vos.member.user.LoginVo;
import io.junpring.coupang.vos.member.user.LogoutVo;
import io.junpring.coupang.vos.member.user.RegisterVo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class UserService {
    private final IUserMapper userMapper;

    @Autowired
    public UserService(IUserMapper userMapper) {
        this.userMapper = userMapper;
    }
    //    public static boolean checkEmail(String input) {
//        return input != null & input.matches("^(?=.{10,50}$)([0-9a-z][0-9a-z_]*[0-9a-z])@([0-9a-z][0-9a-z\\-]*[0-9a-z]\\.)?([0-9a-z][0-9a-z\\-]*[0-9a-z])\\.([a-z]{2,15})(\\.[a-z]{2})?$");
//    }
//    public static boolean checkPassword(String input) {
//        return input != null & input.matches("^([0-9a-zA-Z`~!@#$%^&*()\\\\-_=+\\\\[{\\\\]}\\\\\\\\|;:\\'\",<.>/?]{8,100})$");
//
//    }
    public void extendSession(SessionEntity sessionEntity) { // 연장
        sessionEntity.setUpdatedAt(new Date());
        sessionEntity.setExpiresAt(DateUtils.addMinutes(sessionEntity.getUpdatedAt(), 60));
        this.userMapper.updateSession(sessionEntity);
    }

    public SessionEntity getSession(String key) {
        return this.userMapper.selectSessionByKey(key);
    }

    public UserEntity getUser(String email) {
        return this.userMapper.selectUserByEmail(email);
    }

    public void expireSession(SessionEntity sessionEntity) { // 만료
        sessionEntity.setExpired(true);
        this.userMapper.updateSession(sessionEntity);
    }

    public void login(LoginVo loginVo, HttpServletRequest request) {
        loginVo.setPassword(CryptoUtil.hashSha512(loginVo.getPassword()));
/**      로그인을통해 받을 비밀번호를 해싱 그리고 setPassword */
        UserEntity userEntity = this.userMapper.selectUser(loginVo);
/**     interface IUserMapper 인터페이스에서 UserEntity selectUser(UserEntity userEntity);, UserEntity 타입 메서드이므로 service에서 UserEntity 객체를 생성해서 담아야한다. */
        if (userEntity == null) {
            loginVo.setResult(LoginResult.FAILURE);
            //  SELECT한 회원정보가 null(없으면) FAILURE
            return;
        }

        loginVo.setEmail(userEntity.getEmail());
        loginVo.setPassword(userEntity.getPassword());
        loginVo.setName(userEntity.getName());
        loginVo.setContact(userEntity.getContact());
        loginVo.setAdmin(userEntity.isAdmin());
/**     위에서 UserEntity userEntity = this.userMapper.selectUser(loginVo); 쿼리 SELECT한 값을 보이는데로 userEntity에 담았으니 userEntity에 담긴 SELECT한 값을 매개변수 loginVo로 넘겨줘야한다. */

        this.userMapper.updateSessionExpiredByEmail(loginVo.getEmail());
        // (세션 만료처리)  email의 `expired_flag` = TRUE


//        2 동일한 이메일을 가진 모든 세션이 중복


        // 세션 처리 - Redis
        String sessionKey = String.format("%s%s%s%f%f",
                new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()),
                loginVo.getEmail(),
                loginVo.getPassword(),
                Math.random(),
                Math.random());

/**      1. String.format 이용해서 %s에 (년도,월,일,시간,분,초,밀리초), email, password 담음
 2. %f에 Math.random()으로 랜덤 실수값 담음.
 3. String타입 변수 sessionKey에 담음.
 */
        String userAgent = request.getHeader("User-Agent");
/**      브라우져 정보 가져오기 (HttpServletRequest 객체 선언 필요) => String타입 userAgent변수에 담음 */


        sessionKey = CryptoUtil.hashSha512(sessionKey);
        userAgent = CryptoUtil.hashSha512(userAgent);
/**      sessionKey, userAgent 키 코드를 해싱해서 마무리. */
        SessionEntity sessionEntity = new SessionEntity(); // 코드를 담을 sessionEntity 객체생성
        sessionEntity.setCreatedAt(new Date()); // 현재 날짜,시간 등 createAt에 담음
        sessionEntity.setUpdatedAt(sessionEntity.getCreatedAt()); // 현재시간 업데이트
        sessionEntity.setExpiresAt(DateUtils.addMinutes(sessionEntity.getCreatedAt(), 60)); // DateUtils.addMinutes(sessionEntity.생성시간(), 60분) // (생성시간 + 60분)
        sessionEntity.setExpired(false); // 강제로 isExpired에 false 지정.
        sessionEntity.setUserEmail(loginVo.getEmail());
        /** userEntity에 담긴 email을 sessionEntity에 담음. */
        sessionEntity.setKey(sessionKey);
        sessionEntity.setUa(userAgent);
        /** 해싱이 마무리된 sessionKey, userAgent 를 sessionEntity에 담음. */
        this.userMapper.insertSession(sessionEntity); // sessionEntity로 바로접근 (vo 없음)
//      sessionKey 만들어서 insert!

        loginVo.setSessionEntity(sessionEntity);
//      service에서 구현한 로직들을 loginVo에 선언된 SessionEntity에 set 담음
        loginVo.setResult(LoginResult.SUCCESS);
    }

    public void register(RegisterVo registerVo) {
        registerVo.setPassword(CryptoUtil.hashSha512(registerVo.getPassword()));
//      회원가입으로 가져온 password를 해싱 setPassword

        if (this.userMapper.selectUserCountBy(registerVo.getEmail()) > 0) {
            registerVo.setResult(RegisterResult.DUPLICATE);
            return;
/**     email 중복 여부 : sql에서 WHERE email = #{email}이 중복되면 중복된 수 만큼 count가 늘어남, 그럼 count가 0보다 크기때문에 DUPLICATE */
        }

//        if (!checkEmail(registerVo.getEmail())) {
//            registerVo.setResult(RegisterResult.FAILURE);
//            return;
//        }
//        if (!checkPassword(registerVo.getPassword())) {
//            registerVo.setResult(RegisterResult.FAILURE);
//            return;
//        }
        if (this.userMapper.insertUser(registerVo) == 0) {
            registerVo.setResult(RegisterResult.FAILURE);
            return;
/**     int insertUser(UserEntity userEntity)가 0과 같다면 FAILURE */
        }
        registerVo.setResult(RegisterResult.SUCCESS);
    }
}

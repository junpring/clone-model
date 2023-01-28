package io.junpring.coupang.interceptors;

import io.junpring.coupang.entities.member.SessionEntity;
import io.junpring.coupang.entities.member.UserEntity;
import io.junpring.coupang.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    // Interceptor에서 초기화가 안되니까 @Autowired 붙혀넣어줌

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         *   1. 쿠키에서 sk 값 가져오기
         2. <1>의 sk를 이용하여 DB에서 SessionEntity 긁어오기. 단, 만료되지않은거만
         3. <2>에서 돌아온 SessionEntity가  null이거나 userEmail이 없거나 하면 로그인안한거.
         4. <2>에서 돌아온 SessionEntity의 userEmail이 있고 이 email로 userEntity를 긁어왓더니 정상이다 = 로그인된상태
         5. <4>에서 긁어온 UserEntity를 request 객체에 Attrbute로 추가하면 다  ok
         */

        Cookie sessionKeyCookie = null; // 쿠키 생성 후 = null
        if (request.getCookies() != null) { // 요청받은 쿠키가 null이 아니면
            for (Cookie cookie : request.getCookies()) { // 요청받은 쿠키들 배열길이만큼 반복 돌림
                if (cookie.getName().equals("sk")) { // 조건) 쿠키이름이 "sk"면
                    sessionKeyCookie = cookie; // sessionKeyCookie 에 Cookie 배열항목이 대입됨.
                    break;
                }
            }
//            번외)
//            sessionKeyCookie = Arrays.stream(request.getCookies())
//                    .filter(x -> x.getName().equals("sk"))
//                    .findFirst()
//                    .orElse(null);
//            ~.findFirst() 는 Optional<cookie>를 돌려줌
//            Optional<T>
        }
        if (sessionKeyCookie != null && sessionKeyCookie.getValue() != null) {
            String sessionKey = sessionKeyCookie.getValue();
            SessionEntity sessionEntity = this.userService.getSession(sessionKey);
            if (sessionEntity != null && sessionEntity.getUserEmail() != null) {
                UserEntity userEntity = this.userService.getUser(sessionEntity.getUserEmail());
                if (userEntity != null && userEntity.getEmail() != null) {
                    this.userService.extendSession(sessionEntity);
                    request.setAttribute("sessionEntity", sessionEntity);
                    request.setAttribute("userEntity", userEntity);
                }
            }
        }

        if (request.getAttribute("userEntity") == null && sessionKeyCookie != null) {
            sessionKeyCookie.setMaxAge(0); // Cookie는 remove나 delete같은게 없어서 setMaxAge를 0으로 둠으로써 삭제할 수 있다. 쿠키수명 0으로 만듬  == 제거
        }

        return true;
    }
}

package io.junpring.coupang.controllers;

import io.junpring.coupang.entities.member.SessionEntity;
import io.junpring.coupang.entities.member.UserEntity;
import io.junpring.coupang.entities.product.ProductEntity;
import io.junpring.coupang.entities.shopping.OrderEntity;
import io.junpring.coupang.entities.shopping.OrderStatusEntity;
import io.junpring.coupang.enums.member.user.LoginResult;
import io.junpring.coupang.services.ProductService;
import io.junpring.coupang.services.ShoppingService;
import io.junpring.coupang.services.UserService;
import io.junpring.coupang.vos.member.user.LoginVo;
import io.junpring.coupang.vos.member.user.LogoutVo;
import io.junpring.coupang.vos.member.user.RegisterVo;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Controller
@RequestMapping(value = "/user")

public class UserController {
    private final UserService userService;
    private final ProductService productService;
    private final ShoppingService shoppingService;

    @Autowired
    public UserController(UserService userService, ProductService productService, ShoppingService shoppingService) {
        this.userService = userService;
        this.productService = productService;
        this.shoppingService = shoppingService;
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView getLogin(
            ModelAndView modelAndView
    ) {
        modelAndView.setViewName("user/login");
        return modelAndView;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ModelAndView postLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            LoginVo loginVo,
            ModelAndView modelAndView

    ) {
        loginVo.setResult(null);
        this.userService.login(loginVo, request);
        if (loginVo.getResult() == LoginResult.SUCCESS) {
            Cookie sessionKeyCookie = new Cookie("sk", loginVo.getSessionEntity().getKey());
//          new 쿠키생성("이름", service에서 sessionKey이 해싱된 키)
            sessionKeyCookie.setPath("/");
//          위 설정하지않으면 기본 path : 현재주소 (/user/**) 이 주소에만 사용가능, /는 다 사용가능.
            response.addCookie(sessionKeyCookie);
//          response에 생성한 쿠키를 추가. (응답보낼 쿠키?)
        }
        modelAndView.addObject("loginVo", loginVo);
        modelAndView.setViewName("user/login");
        return modelAndView;
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ModelAndView getLogout(
//            SessionStatus sessionStatus,
            HttpServletRequest request,
            ModelAndView modelAndView
    ) {
        // 1. 어디선가 'UserEntity' 타입의 객체 받아와서 이가 가진 email이 null이 아니라면 'sessions' 테이블에서 이 이메일을 가진 유효한 세션 만료시키기.
        // 2. `sk`라는 이름을 가지는 쿠키 삭제

//        sessionStatus.setComplete(); // 필요없어짐 이 프로젝트는 레거시세션에 의존적이지 않기때문에. //       setComplete : 세션어트리뷰트에 저장된 세션들이 싹날아감
        SessionEntity sessionEntity = null;
        if (request.getAttribute("sessionEntity") instanceof SessionEntity) {
            sessionEntity = (SessionEntity) request.getAttribute("sessionEntity");
            this.userService.expireSession(sessionEntity);
        }

        System.out.println("logOutEmail : " + sessionEntity.getUserEmail());

        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ModelAndView postLogout(
            ModelAndView modelAndView
    ) {

        modelAndView.setViewName("redirect:/");

        return modelAndView;
    }

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public ModelAndView getRegister(
            ModelAndView modelAndView
    ) {
        modelAndView.setViewName("user/register");
        return modelAndView;
    }


    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ModelAndView postRegister(
            RegisterVo registerVo,
            ModelAndView modelAndView
    ) {
        registerVo.setAdmin(false);
        registerVo.setResult(null);
        //      위 두개는 강제로 값을 지정!

        this.userService.register(registerVo);
        modelAndView.addObject("registerVo", registerVo);
        modelAndView.setViewName("user/register");
        return modelAndView;
    }

    @RequestMapping(value = "my", method = RequestMethod.GET)
    public ModelAndView getMy(@RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
                              ModelAndView modelAndView) {
        if (userEntity == null) {
            modelAndView.setViewName("redirect:/user/login");
        } else {
            List<Pair<OrderEntity, ProductEntity>> pairList = new ArrayList<>();
            OrderEntity[] orderEntities = userEntity.isAdmin()
                    ? this.shoppingService.getOrdersAll()
                    : this.shoppingService.getOrdersOf(userEntity);
            for (OrderEntity orderEntity : orderEntities) {
                ProductEntity productEntity = this.productService.getProductByIndex(orderEntity.getProductIndex());
                ImmutablePair<OrderEntity, ProductEntity> pair = new ImmutablePair<>(orderEntity, productEntity);
                pairList.add(pair);
            }

            HashMap<Integer, String> orderStatuses = new HashMap<>();
            OrderStatusEntity[] orderStatusEntities = this.shoppingService.getOrderStatuses();
            for (OrderStatusEntity orderStatusEntity : orderStatusEntities) {
                orderStatuses.put(orderStatusEntity.getIndex(), orderStatusEntity.getText());
                System.out.println(orderStatusEntity.getIndex());
                System.out.println(orderStatusEntity.getText());
            }
            modelAndView.addObject("orderStatuses", orderStatuses);
            modelAndView.addObject("pairList", pairList);
            modelAndView.setViewName("user/my");
        }
        return modelAndView;
    }
}

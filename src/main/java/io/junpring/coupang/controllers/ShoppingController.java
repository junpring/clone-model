package io.junpring.coupang.controllers;

import io.junpring.coupang.entities.member.UserEntity;
import io.junpring.coupang.entities.product.ProductEntity;
import io.junpring.coupang.entities.shopping.CartEntity;
import io.junpring.coupang.entities.shopping.OrderEntity;
import io.junpring.coupang.entities.shopping.ReviewEntity;
import io.junpring.coupang.enums.ExtendedResult;
import io.junpring.coupang.enums.GeneralResult;
import io.junpring.coupang.enums.shopping.cart.AddResult;
import io.junpring.coupang.services.ProductService;
import io.junpring.coupang.services.ShoppingService;
import io.junpring.coupang.vos.shopping.AddVo;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller(value = "io.junpring.coupang.controllers.ShoppingController")
@RequestMapping(value = "/shopping")
public class ShoppingController {
    private final ShoppingService shoppingService;
    private final ProductService productService;

    @Autowired
    public ShoppingController(ShoppingService shoppingService, ProductService productService) {
        this.shoppingService = shoppingService;
        this.productService = productService;
    }

    @RequestMapping(value = "cart", method = RequestMethod.GET)
    public ModelAndView getCart(@RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
                                ModelAndView modelAndView) {
        if (userEntity == null) {
            modelAndView.setViewName("redirect:/user/login");
        } else {
            // Tuple 튜플
            List<Pair<CartEntity, ProductEntity>> pairList = new ArrayList<>();
            CartEntity[] cartEntities = this.shoppingService.getCartsOf(userEntity);
            int totalProduct = 0;
            int totalShipping = 0;
            for (CartEntity cartEntity : cartEntities) {
                ProductEntity productEntity = this.productService.getProductByIndex(cartEntity.getProductIndex());
                ImmutablePair<CartEntity, ProductEntity> pair = new ImmutablePair<>(cartEntity, productEntity);
                pairList.add(pair);
                totalProduct += productEntity.getPrice() * cartEntity.getCount();
                if (productEntity.getDelivery().equals("normal")) {
                    totalShipping += 3000;
                }
            }
            // totalProduct = pairList.stream().mapToInt(x -> x.getRight().getPrice() * x.getLeft().getCount()).sum();
            modelAndView.addObject("totalProduct", totalProduct);
            modelAndView.addObject("totalShipping", totalShipping);
            modelAndView.addObject("total", totalProduct + totalShipping);
            modelAndView.addObject("pairList", pairList);
            modelAndView.setViewName("shopping/cart");
        }
        return modelAndView;
    }

    @RequestMapping(value = "cart/add", method = RequestMethod.POST)
    @ResponseBody
    public String postCartAdd(@RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
                              @RequestParam(value = "productIndex", required = true) int productIndex,
                              @RequestParam(value = "count", required = true) int count) {
        AddVo addVo = new AddVo();
        if (userEntity == null) {
            addVo.setResult(AddResult.NOT_SIGNED);
        } else {
            addVo.setUserEmail(userEntity.getEmail());
            addVo.setProductIndex(productIndex);
            addVo.setCount(count);
            this.shoppingService.addCart(addVo);
        }
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("result", addVo.getResult().name());
        return jsonResponse.toString();
    }

    @RequestMapping(value = "cart/delete", method = RequestMethod.GET)
    public ModelAndView getCartDelete(@RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
                                      @RequestParam(value = "index", required = true) int productIndex,
                                      ModelAndView modelAndView) {
        if (userEntity == null) {
            modelAndView.setViewName("redirect:/user/login");
        } else {
            this.shoppingService.deleteCartsOf(userEntity, productIndex);
            modelAndView.setViewName("redirect:/shopping/cart");
        }
        return modelAndView;
    }

    @RequestMapping(value = "order", method = RequestMethod.GET)
    public ModelAndView getOrder(@RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
                                 ModelAndView modelAndView) {
        if (userEntity == null) {
            modelAndView.setViewName("redirect:/user/login");
        } else {
            modelAndView.setViewName("shopping/order");
        }
        return modelAndView;
    }

    @RequestMapping(value = "order", method = RequestMethod.POST)
    public ModelAndView postOrder(@RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
                                  @RequestParam(value = "pid", required = false) Optional<Integer> productIndex,
                                  @RequestParam(value = "count", required = false) Optional<Integer> count,
                                  ModelAndView modelAndView) {
        if (userEntity == null) {
            modelAndView.setViewName("redirect:/user/login");
        } else if (productIndex.orElse(0) == 0) {  // 장바구니 전체 구매
            this.shoppingService.orderAll(userEntity);
            modelAndView.setViewName("redirect:/shopping/order");
        } else { // 특정 상품 구매
            this.shoppingService.orderSpecific(userEntity, productIndex.get(), count.orElse(1));
            modelAndView.setViewName("redirect:/shopping/order");
        }
        return modelAndView;
    }

    @RequestMapping(value = "order/status/modify", method = RequestMethod.POST)
    @ResponseBody
    public String postOrderStatusModify(
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "orderIndex", required = true) int orderIndex,
            @RequestParam(value = "orderStatusIndex", required = true) int orderStatusIndex,
            HttpServletResponse response
    ) {
        System.out.println("orderStatusIndex: " + orderStatusIndex);
        System.out.println("orderIndex: " + orderIndex);
        if (userEntity == null || !userEntity.isAdmin()) {
            response.setStatus(404);
            return null;
        }
        GeneralResult result;
        OrderEntity orderEntity = this.shoppingService.getOrder(orderIndex);
        if (orderEntity == null) {
            result = GeneralResult.NOT_FOUND;
        } else {
            orderEntity.setOrderStatusIndex(orderStatusIndex);
            result = this.shoppingService.modifyOrder(orderEntity)
                    ? GeneralResult.SUCCESS
                    : GeneralResult.FAILURE;
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put("result", result.name());
        return responseJson.toString();
    }

    @RequestMapping(value = "review/write", method = RequestMethod.GET)
    public ModelAndView getReviewWrite(
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "oid", required = true) int orderIndex,
            ModelAndView modelAndView
    ) {
        if (userEntity == null) {
            modelAndView.setViewName("redirect:/user/login");
            return modelAndView;
        }
        Enum<?> result;
        OrderEntity orderEntity = this.shoppingService.getOrder(orderIndex);
        if (orderEntity == null) {
            result = GeneralResult.NOT_FOUND;
        } else if (!userEntity.getEmail().equals(orderEntity.getUserEmail())) { // 주문자 이메일이 일치하지 않으면
            result = ExtendedResult.NOT_ALLOWED;
        } else {
            ProductEntity productEntity = this.productService.getProductByIndex(orderEntity.getProductIndex());
            result = GeneralResult.SUCCESS;
            modelAndView.addObject("productEntity", productEntity);
        }
        modelAndView.addObject("result", result);
        modelAndView.setViewName("shopping/review/write");
        return modelAndView;
    }

    @RequestMapping(value = "review/write", method = RequestMethod.POST)
    public ModelAndView postReviewWrite(
            @RequestAttribute(value = "userEntity", required = false) UserEntity userEntity,
            @RequestParam(value = "oid", required = true) int orderIndex,
            @RequestParam(value = "rate", required = true) int rate,
            @RequestParam(value = "comment", required = true, defaultValue = "") String comment,
            ModelAndView modelAndView
    ) {
        if (userEntity == null) {
            modelAndView.setViewName("redirect:/user/login");
            return modelAndView;
        }
        Enum<?> result;
        OrderEntity orderEntity = this.shoppingService.getOrder(orderIndex);

        if (orderEntity == null) {
            result = GeneralResult.NOT_FOUND;
        } else if (!userEntity.getEmail().equals(orderEntity.getUserEmail())) {
            result = ExtendedResult.NOT_ALLOWED;
        } else if (this.shoppingService.reviewExists(orderEntity)) {
            result = ExtendedResult.DUPLICATE;
        } else {
            ReviewEntity reviewEntity = new ReviewEntity();
            reviewEntity.setUserEmail(userEntity.getEmail());
            reviewEntity.setProductIndex(orderEntity.getProductIndex());
            reviewEntity.setOrderIndex(orderEntity.getIndex());
            reviewEntity.setRate(rate);
            reviewEntity.setComment(comment);
            this.shoppingService.putReview(reviewEntity);

            modelAndView.setViewName("redirect:/product/detail/" + orderEntity.getProductIndex());
            return modelAndView;
        }
        if (orderEntity != null) {
            ProductEntity productEntity = this.productService.getProductByIndex(orderEntity.getProductIndex());
            modelAndView.addObject("productEntity", productEntity);
        }

        modelAndView.addObject("result", result);
        modelAndView.setViewName("/shopping/review/write");
        return modelAndView;
    }
}

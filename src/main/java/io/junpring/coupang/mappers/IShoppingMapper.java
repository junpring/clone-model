package io.junpring.coupang.mappers;

import io.junpring.coupang.entities.shopping.CartEntity;
import io.junpring.coupang.entities.shopping.OrderEntity;
import io.junpring.coupang.entities.shopping.OrderStatusEntity;
import io.junpring.coupang.entities.shopping.ReviewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@SuppressWarnings("UnusedReturnValue")
@Mapper
public interface IShoppingMapper {
    int deleteCartByEmailAndProductIndex(@Param(value = "email") String email,
                                         @Param(value = "productIndex") int productIndex);

    int insertCart(CartEntity cartEntity);

    int insertOrder(OrderEntity orderEntity);
    int insertReview(ReviewEntity reviewEntity);

    CartEntity[] selectCartsByEmail(@Param(value = "email") String email);

    OrderEntity selectOrder(@Param(value = "index") int index);

    OrderEntity[] selectOrders();

    OrderEntity[] selectOrdersByEmail(@Param(value = "email") String email);

    OrderStatusEntity[] selectOrderStatuses();
    ReviewEntity[] selectReviewsByProductIndex(@Param(value = "productIndex") int productIndex);
    int selectReviewCountByOrderIndex(@Param(value = "orderIndex") long orderIndex);

    int updateOrder(OrderEntity orderEntity);
}

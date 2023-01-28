package io.junpring.coupang.services;

import com.sun.org.apache.xpath.internal.operations.Or;
import io.junpring.coupang.entities.member.UserEntity;
import io.junpring.coupang.entities.product.ProductEntity;
import io.junpring.coupang.entities.product.StockEntity;
import io.junpring.coupang.entities.shopping.CartEntity;
import io.junpring.coupang.entities.shopping.OrderEntity;
import io.junpring.coupang.entities.shopping.OrderStatusEntity;
import io.junpring.coupang.entities.shopping.ReviewEntity;
import io.junpring.coupang.enums.shopping.cart.AddResult;
import io.junpring.coupang.mappers.IProductMapper;
import io.junpring.coupang.mappers.IShoppingMapper;
import io.junpring.coupang.vos.shopping.AddVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ShoppingService {
    private final IProductMapper productMapper;
    private final IShoppingMapper shoppingMapper;

    @Autowired
    public ShoppingService(IProductMapper productMapper, IShoppingMapper shoppingMapper) {
        this.productMapper = productMapper;
        this.shoppingMapper = shoppingMapper;
    }


    public void addCart(AddVo addVo) {
        ProductEntity productEntity = this.productMapper.selectProductByIndex(addVo.getProductIndex());
        if (productEntity == null) {
            addVo.setResult(AddResult.NOT_FOUND);
            return;
        }
        if (this.shoppingMapper.insertCart(addVo) == 0) {
            addVo.setResult(AddResult.FAILURE);
            return;
        }
        addVo.setResult(AddResult.SUCCESS);
    }

    public void deleteCartsOf(UserEntity userEntity, int productIndex) {
        this.shoppingMapper.deleteCartByEmailAndProductIndex(userEntity.getEmail(), productIndex);
    }

    public CartEntity[] getCartsOf(UserEntity userEntity) {
        return this.shoppingMapper.selectCartsByEmail(userEntity.getEmail());
    }

    public OrderEntity getOrder(int orderIndex) {
        return this.shoppingMapper.selectOrder(orderIndex);
    }

    public OrderEntity[] getOrdersAll() {
        return this.shoppingMapper.selectOrders();
    }

    public OrderEntity[] getOrdersOf(UserEntity userEntity) {
        return this.shoppingMapper.selectOrdersByEmail(userEntity.getEmail());
    }

    public OrderStatusEntity[] getOrderStatuses() {
        return this.shoppingMapper.selectOrderStatuses();
    }
    public ReviewEntity[] getReviews(ProductEntity productEntity) {
        return this.shoppingMapper.selectReviewsByProductIndex(productEntity.getIndex());
    }

    public boolean modifyOrder(OrderEntity orderEntity) {
        return this.shoppingMapper.updateOrder(orderEntity) > 0;
    }

    @Transactional
    public void orderAll(UserEntity userEntity) {
        CartEntity[] cartEntities = this.shoppingMapper.selectCartsByEmail(userEntity.getEmail());
        Date currentDate = new Date();
        for (CartEntity cartEntity : cartEntities) {
            this.orderSpecific(userEntity, cartEntity.getProductIndex(), cartEntity.getCount(), currentDate);
            this.shoppingMapper.deleteCartByEmailAndProductIndex(userEntity.getEmail(), cartEntity.getProductIndex());
        }
    }

    public void orderSpecific(UserEntity userEntity, int productIndex, int count) {
        this.orderSpecific(userEntity, productIndex, count, new Date());
    }

    public void orderSpecific(UserEntity userEntity, int productIndex, int count, Date currentDate) {
        ProductEntity productEntity = this.productMapper.selectProductByIndex(productIndex);
        int priceProduct = productEntity.getPrice();
        int priceShipping = productEntity.getDelivery().equals("normal") ? 3000 : 0;

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCreatedAt(currentDate);
        orderEntity.setUserEmail(userEntity.getEmail());
        orderEntity.setProductIndex(productIndex);
        orderEntity.setCount(count);
        orderEntity.setOrderStatusIndex(1);
        orderEntity.setPriceProduct(priceProduct);
        orderEntity.setPriceShipping(priceShipping);
        this.shoppingMapper.insertOrder(orderEntity);

        StockEntity stockEntity = new StockEntity();
        stockEntity.setCreatedAt(currentDate);
        stockEntity.setProductIndex(productIndex);
        stockEntity.setCount(count * -1);
        this.productMapper.insertStock(stockEntity);
    }

    public void putReview(ReviewEntity reviewEntity) {
        this.shoppingMapper.insertReview(reviewEntity);
    }

    public boolean reviewExists(OrderEntity orderEntity) {
        return this.shoppingMapper.selectReviewCountByOrderIndex(orderEntity.getIndex()) > 0;
    }
}
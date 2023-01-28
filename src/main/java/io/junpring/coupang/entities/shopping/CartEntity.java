package io.junpring.coupang.entities.shopping;

public class CartEntity {
    private Long index; // index
    private String userEmail; // 유저 아이디
    private int productIndex; // 상품 번호
    private int count; // 장바구니 count 개수

    public CartEntity() {
    }

    public CartEntity(Long index, String userEmail, int productIndex, int count) {
        this.index = index;
        this.userEmail = userEmail;
        this.productIndex = productIndex;
        this.count = count;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getProductIndex() {
        return productIndex;
    }

    public void setProductIndex(int productIndex) {
        this.productIndex = productIndex;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

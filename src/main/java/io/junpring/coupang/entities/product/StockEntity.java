package io.junpring.coupang.entities.product;

import java.util.Date;

public class StockEntity {
    private Long index; // 인덱스 번호
    private Date createdAt; // 생성 시간
    private int productIndex; // 상품 번호
    private int count; // 카운트

    public StockEntity() {
    }

    public StockEntity(Long index, Date createdAt, int productIndex, int count) {
        this.index = index;
        this.createdAt = createdAt;
        this.productIndex = productIndex;
        this.count = count;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

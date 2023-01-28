package io.junpring.coupang.dtos.product;

import io.junpring.coupang.entities.product.ProductEntity;

public class ProductDTO extends ProductEntity {
    private int count; // 숫자를 세는 카운트 : DTO는 확장
    private int reviewCount;
    private double reviewRate;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public double getReviewRate() {
        return reviewRate;
    }

    public void setReviewRate(double reviewRate) {
        this.reviewRate = reviewRate;
    }
}

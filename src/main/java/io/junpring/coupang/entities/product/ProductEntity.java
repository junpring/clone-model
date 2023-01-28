package io.junpring.coupang.entities.product;

public class ProductEntity {
    private int index; // 인덱스 번호
    private String title; // 상품 이름
    private int price; // 상품 가격
    private String delivery; // 상품 배송방식 ex) roketFresh
    private String content; // 상품 내용
    private String thumbnailId; // 썸네일 id
    private int categoryIndex;

    public ProductEntity() {
    }

    public ProductEntity(int index, String title, int price, String delivery, String content, String thumbnailId, int categoryIndex) {
        this.index = index;
        this.title = title;
        this.price = price;
        this.delivery = delivery;
        this.content = content;
        this.thumbnailId = thumbnailId;
        this.categoryIndex = categoryIndex;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
    }
}

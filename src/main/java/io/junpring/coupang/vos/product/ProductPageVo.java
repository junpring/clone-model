package io.junpring.coupang.vos.product;

import io.junpring.coupang.entities.product.ProductEntity;

public class ProductPageVo extends ProductEntity {

    private String criteria; // 검색 기준
    private String keyword; // 검색 내용
    private int productPerPage; // 에 페이지당 보여질 글 개수
    private int requestPage; // 요청한 현재 페이지 ex)6
    private int startPage; // 현재 페이지에서 처음 페이지 ex)1
    private int endPage; // 현재 페이지에서 마지막 페이지 ex)9
    private int maxPage; // 최대 페이지 ex)100
    private int minPage; // 최소 페이지 ex)1

    public String getCriteria() {
        return criteria;
    }
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getProductPerPage() {
        return productPerPage;
    }

    public void setProductPerPage(int productPerPage) {
        this.productPerPage = productPerPage;
    }

    public int getRequestPage() {
        return requestPage;
    }

    public void setRequestPage(int requestPage) {
        this.requestPage = requestPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public int getMinPage() {
        return minPage;
    }

    public void setMinPage(int minPage) {
        this.minPage = minPage;
    }

}

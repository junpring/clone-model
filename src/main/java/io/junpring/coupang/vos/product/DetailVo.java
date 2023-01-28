package io.junpring.coupang.vos.product;

import io.junpring.coupang.entities.product.ProductEntity;
import io.junpring.coupang.enums.product.DetailResult;

public class DetailVo extends ProductEntity {
    private DetailResult result;

    public DetailResult getResult() {
        return result;
    }

    public void setResult(DetailResult result) {
        this.result = result;
    }
}

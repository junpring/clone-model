package io.junpring.coupang.vos.product;

import io.junpring.coupang.entities.product.ProductEntity;
import io.junpring.coupang.enums.product.AddResult;

public class AddVo extends ProductEntity {
    private AddResult result;

    public AddResult getResult() {
        return result;
    }

    public void setResult(AddResult result) {
        this.result = result;
    }
}

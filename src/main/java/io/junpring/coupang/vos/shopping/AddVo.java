package io.junpring.coupang.vos.shopping;

import io.junpring.coupang.entities.shopping.CartEntity;
import io.junpring.coupang.enums.shopping.cart.AddResult;

public class AddVo extends CartEntity {
    private AddResult result;

    public AddResult getResult() {
        return result;
    }

    public void setResult(AddResult result) {
        this.result = result;
    }
}

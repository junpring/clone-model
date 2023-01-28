package io.junpring.coupang.interfaces;


public interface IResult <T extends Enum<?>>{
    T getResult();

    void setResult(T t);
}

package io.junpring.coupang.entities.product;

public class CategoryEntity {
    private int index;
    private String name;

    public CategoryEntity() {
    }

    public CategoryEntity(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

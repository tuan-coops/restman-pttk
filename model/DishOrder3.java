package com.restman.model;

public record DishOrder3(int id, Dish3 dish, int quantity, long price) {
    public long amount() {
        return price * quantity;
    }
}
